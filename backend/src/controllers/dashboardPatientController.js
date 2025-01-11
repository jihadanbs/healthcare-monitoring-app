const mongoose = require('mongoose');
const MedicalRecord = require('../models/MedicalRecord');
const Consultation = require('../models/Consultation');
const Notification = require('../models/Notification');
const User = require('../models/User');

exports.getDashboardData = async (req, res) => {
    try {
        const userId = req.user.id;

        // Fetch health summary
        const medicalRecords = await MedicalRecord.find({ userId })
            .sort({ createdAt: -1 })
            .limit(5);
        
        const healthSummary = medicalRecords.map(record => 
            `${record.diagnosis} on ${record.createdAt.toLocaleDateString()}`
        ).join(', ');

        // Fetch upcoming appointments
        const upcomingAppointments = await Consultation.find({ 
            userId, 
            status: 'scheduled',
            date: { $gte: new Date() }
        }).sort({ date: 1 });

        // Fetch prescribed medicines
        const prescribedMedicines = await MedicalRecord.find({ 
            userId, 
            'prescription.status': { $ne: 'PURCHASED' }
        }).select('prescription');

        // Fetch notifications
        const notifications = await Notification.find({ 
            userId, 
            isRead: false 
        }).sort({ createdAt: -1 });

        res.json({
            healthSummary,
            upcomingAppointments,
            prescribedMedicines,
            notifications
        });
    } catch (error) {
        res.status(500).json({ message: 'Error fetching dashboard data', error: error.message });
    }
};

exports.getPrescribedMedicines = async (req, res) => {
    try {
        const userId = req.user.id;
        const prescribedMedicines = await MedicalRecord.aggregate([
            { $match: { patient: new mongoose.Types.ObjectId(userId) } },
            { $unwind: "$prescription" },
            { $match: { "prescription.status": { $ne: "PURCHASED" } } },
            { $group: {
                _id: "$_id",
                prescription: { $push: "$prescription" }
            }}
        ]);

        // Transform data to match frontend model
        const medicines = prescribedMedicines.flatMap(record => 
            record.prescription.map(item => ({
                id: item._id.toString(),
                medicine: item.medicine,
                dosage: item.dosage,
                frequency: item.frequency,
                status: item.status,
                price: item.price
            }))
        );

        res.json(medicines);
    } catch (error) {
        res.status(500).json({ 
            message: 'Error fetching prescribed medicines', 
            error: error.message 
        });
    }
};

exports.updateMedicineStatus = async (req, res) => {
    try {
        const medicineId = req.params.id;
        const { status } = req.body;

        console.log('Updating medicine status:', {
            medicineId,
            status,
            body: req.body
        });

        // Validate status
        const validStatuses = ['NOT_PURCHASED', 'IN_PROGRESS', 'PURCHASED'];
        if (!validStatuses.includes(status)) {
            return res.status(400).json({ 
                message: 'Invalid status value' 
            });
        }

        // Find and update the specific prescription item in medical record
        const updatedRecord = await MedicalRecord.findOneAndUpdate(
            { 'prescription._id': medicineId },
            { 
                $set: { 
                    'prescription.$.status': status 
                } 
            },
            { new: true }
        );

        if (!updatedRecord) {
            return res.status(404).json({ 
                message: 'Medicine not found in prescriptions' 
            });
        }

        // Find the updated prescription item
        const updatedPrescription = updatedRecord.prescription.find(
            p => p._id.toString() === medicineId
        );

        res.json({
            message: 'Medicine status updated successfully',
            prescription: updatedPrescription
        });

    } catch (error) {
        console.error('Error updating medicine status:', error);
        res.status(500).json({ 
            message: 'Error updating medicine status', 
            error: error.message,
            stack: error.stack
        });
    }
};

exports.getCheckoutMedicines = async (req, res) => {
    try {
        const userId = req.user.id;

        // First verify the user is a patient
        const user = await User.findById(userId);
        if (!user || user.role !== 'patient') {
            return res.status(403).json({
                success: false,
                message: 'Only patients can access checkout items'
            });
        }

        // Fetch medicines with IN_PROGRESS status for the specific patient
        const checkoutItems = await MedicalRecord.aggregate([
            {
                $match: {
                    patient: new mongoose.Types.ObjectId(userId)
                }
            },
            {
                $unwind: "$prescription"
            },
            {
                $match: {
                    "prescription.status": "IN_PROGRESS"
                }
            },
            {
                $lookup: {
                    from: "users",
                    localField: "doctor",
                    foreignField: "_id",
                    as: "doctorDetails"
                }
            },
            {
                $unwind: "$doctorDetails"
            },
            {
                $group: {
                    _id: "$_id",
                    consultationDate: { $first: "$createdAt" },
                    doctorName: { $first: "$doctorDetails.name" },
                    doctorSpecialization: { $first: "$doctorDetails.profile.specialization" },
                    prescription: {
                        $push: {
                            id: "$prescription._id",
                            medicine: "$prescription.medicine",
                            dosage: "$prescription.dosage",
                            frequency: "$prescription.frequency",
                            price: "$prescription.price",
                            status: "$prescription.status"
                        }
                    },
                    totalAmount: {
                        $sum: "$prescription.price"
                    }
                }
            },
            {
                $project: {
                    _id: 1,
                    consultationDate: 1,
                    doctorName: 1,
                    doctorSpecialization: 1,
                    prescription: 1,
                    totalAmount: 1
                }
            }
        ]);

        // Format response to match frontend expectations
        const response = checkoutItems.map(item => ({
            recordId: item._id,
            consultationDate: item.consultationDate,
            doctor: {
                name: item.doctorName,
                specialization: item.doctorSpecialization
            },
            medicines: item.prescription,
            totalAmount: item.totalAmount
        }));

        res.json({
            success: true,
            data: response
        });

    } catch (error) {
        console.error('Error in getCheckoutMedicines:', error);
        res.status(500).json({
            success: false,
            message: 'Error fetching checkout medicines',
            error: error.message
        });
    }
};

exports.updateCheckoutStatus = async (req, res) => {
    try {
        const { recordId, medicineIds, status } = req.body;
        const userId = req.user.id;

        // Verify user is a patient
        const user = await User.findById(userId);
        if (!user || user.role !== 'patient') {
            return res.status(403).json({
                success: false,
                message: 'Only patients can update checkout status'
            });
        }

        // Update multiple medicines status
        const updateResult = await MedicalRecord.updateMany(
            {
                _id: recordId,
                patient: userId,
                'prescription._id': { $in: medicineIds.map(id => new mongoose.Types.ObjectId(id)) }
            },
            {
                $set: {
                    'prescription.$[elem].status': status
                }
            },
            {
                arrayFilters: [{ 'elem._id': { $in: medicineIds.map(id => new mongoose.Types.ObjectId(id)) } }],
                new: true
            }
        );

        if (updateResult.modifiedCount === 0) {
            return res.status(404).json({
                success: false,
                message: 'No medicines found to update'
            });
        }

        res.json({
            success: true,
            message: `Successfully updated ${updateResult.modifiedCount} medicines`,
            modifiedCount: updateResult.modifiedCount
        });

    } catch (error) {
        console.error('Error in updateCheckoutStatus:', error);
        res.status(500).json({
            success: false,
            message: 'Error updating checkout status',
            error: error.message
        });
    }
};
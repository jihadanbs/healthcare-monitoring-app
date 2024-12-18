const User = require('../models/User');
const MedicalRecord = require('../models/MedicalRecord');
const Consultation = require('../models/Consultation');
const Notification = require('../models/Notification');

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
        const prescribedMedicines = await MedicalRecord.find({ 
            patient: userId, 
            'prescription.status': { $ne: 'PURCHASED' }
        }).select('prescription');

        // Transform data to match frontend model
        const medicines = prescribedMedicines.flatMap(record => 
            record.prescription.map(item => ({
                id: item._id.toString(),
                medicine: item.medicine,
                dosage: item.dosage,
                frequency: item.frequency,
                status: item.status
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

exports.addMedicine = async (req, res) => {
    try {
        const { 
            patient, 
            doctor, 
            consultation, 
            diagnosis, 
            symptoms, 
            prescription, 
            additionalNotes, 
            followUpDate 
        } = req.body;

        // Validate required fields
        if (!patient || !doctor || !diagnosis) {
            return res.status(400).json({ 
                message: 'Patient, doctor, and diagnosis are required' 
            });
        }

        // Create new medical record
        const newMedicalRecord = new MedicalRecord({
            patient,
            doctor,
            consultation: consultation || null,
            diagnosis,
            symptoms: symptoms || [],
            prescription: prescription || [],
            additionalNotes: additionalNotes || '',
            followUpDate: followUpDate || null
        });

        // Save the medical record
        const savedMedicalRecord = await newMedicalRecord.save();

        res.status(201).json({
            message: 'Medical record added successfully',
            medicalRecord: savedMedicalRecord
        });
    } catch (error) {
        res.status(500).json({ 
            message: 'Error adding medical record', 
            error: error.message 
        });
    }
};

exports.updateMedicineStatus = async (req, res) => {
    try {
        const { medicalRecordId, prescriptionId, status } = req.body;

        // Validate input
        if (!medicalRecordId || !prescriptionId || !status) {
            return res.status(400).json({ 
                message: 'Medical Record ID, Prescription ID, and Status are required' 
            });
        }

        // Validate status
        const validStatuses = ['pending', 'purchased', 'in_progress'];
        if (!validStatuses.includes(status)) {
            return res.status(400).json({ 
                message: 'Invalid medicine status' 
            });
        }

        // Update the medical record
        const updatedMedicalRecord = await MedicalRecord.findOneAndUpdate(
            { 
                _id: medicalRecordId, 
                'prescription._id': prescriptionId 
            },
            { 
                $set: { 'prescription.$.status': status } 
            },
            { 
                new: true // Return the updated document
            }
        );

        if (!updatedMedicalRecord) {
            return res.status(404).json({ 
                message: 'Medical record or prescription not found' 
            });
        }

        res.json({
            message: 'Medicine status updated successfully',
            medicalRecord: updatedMedicalRecord
        });
    } catch (error) {
        res.status(500).json({ 
            message: 'Error updating medicine status', 
            error: error.message 
        });
    }
};
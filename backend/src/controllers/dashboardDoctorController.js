const MedicalRecord = require('../models/MedicalRecord');
const Appointment = require('../models/Appointment');
const User = require('../models/User');

exports.getPatients = async (req, res) => {
    try {
        // Hanya memanggil semua pengguna dengan role "patient"
        const patients = await User.find({ role: 'patient' }).select('name email profile');

        res.status(200).json({
            success: true,
            patients
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error fetching patients',
            error: error.message
        });
    }
};

exports.getPatientHistory = async (req, res) => {
    try {
        const { patientId } = req.params;
        
        const medicalRecords = await MedicalRecord.find({
            doctor: req.user._id,
            patient: patientId
        }).sort({ createdAt: -1 });

        res.status(200).json({
            success: true,
            medicalRecords
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error fetching patient history',
            error: error.message
        });
    }
};

exports.addMedicine = async (req, res) => {
    try {
        // Ambil ID dokter dari req.user yang sudah di-decode oleh middleware
        const doctorId = req.user.id; // Perubahan dari req.user._id menjadi req.user.id
        const { patientId } = req.params;
        const { 
            diagnosis,
            symptoms,
            prescription,
            additionalNotes,
            followUpDate,
            consultationId
        } = req.body;

        // Validasi apakah pasien ada
        const patient = await User.findOne({ _id: patientId, role: 'patient' });
        if (!patient) {
            return res.status(404).json({
                success: false,
                message: 'Pasien tidak ditemukan'
            });
        }

        // Validasi dokter menggunakan ID dari token
        const doctor = await User.findOne({ _id: doctorId, role: 'doctor' });
        if (!doctor) {
            return res.status(404).json({
                success: false,
                message: 'Dokter tidak ditemukan',
                debugInfo: { doctorId, tokenContent: req.user } // Untuk debugging
            });
        }

        // Validasi input yang diperlukan
        if (!diagnosis || !prescription || prescription.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Diagnosis dan minimal satu resep obat harus diisi'
            });
        }

        // Validasi format resep obat
        for (const med of prescription) {
            if (!med.medicine || !med.dosage || !med.frequency || !med.price) {
                return res.status(400).json({
                    success: false,
                    message: 'Setiap resep obat harus memiliki nama obat, dosis, frekuensi, dan harga'
                });
            }
        }

        // Buat record medis baru
        const newMedicalRecord = new MedicalRecord({
            patient: patientId,
            doctor: doctorId,
            consultation: consultationId || null,
            diagnosis,
            symptoms: symptoms || [],
            prescription: prescription.map(med => ({
                ...med,
                status: 'NOT_PURCHASED'
            })),
            additionalNotes: additionalNotes || '',
            followUpDate: followUpDate || null
        });

        // Simpan record medis
        const savedRecord = await newMedicalRecord.save();

        // Populate data pasien dan dokter untuk response
        const populatedRecord = await MedicalRecord.findById(savedRecord._id)
            .populate('patient', 'name email profile')
            .populate('doctor', 'name email profile');

        res.status(201).json({
            success: true,
            message: 'Data medis dan resep obat berhasil ditambahkan',
            medicalRecord: populatedRecord
        });

    } catch (error) {
        console.error('Error in addMedicine:', error);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan saat menambahkan data medis',
            error: error.message
        });
    }
};

exports.getDoctorMedicalRecords = async (req, res) => {
    try {
        const doctorId = req.user.id; // Perhatikan di sini, kita gunakan req.user.id bukan req.user._id
        console.log('ID Dokter dari token:', doctorId);
        
        // Cek apakah ada medical records
        const medicalRecords = await MedicalRecord.find({ doctor: doctorId })
            .populate({
                path: 'patient',
                select: 'name email profile'
            })
            .sort({ createdAt: -1 });
        
        console.log('Jumlah medical records ditemukan:', medicalRecords.length);
        
        if (medicalRecords.length === 0) {
            return res.status(200).json({
                success: true,
                message: 'Belum ada medical records untuk dokter ini',
                medicalRecords: []
            });
        }
        
        res.status(200).json({
            success: true,
            count: medicalRecords.length,
            medicalRecords
        });
        
    } catch (error) {
        console.error('Error pada getDoctorMedicalRecords:', error);
        res.status(500).json({
            success: false,
            message: 'Error mengambil data medical records',
            error: error.message
        });
    }
};

exports.getPatientMedicalRecords = async (req, res) => {
    try {
      const { patientId } = req.params;
      
      // Log untuk debugging
      console.log('Fetching records for:', {
        patientId,
        doctorId: req.user._id
      });
  
      const medicalRecords = await MedicalRecord.find({
        doctor: req.user._id,
        patient: patientId
      })
      .populate('patient', 'name email profile')
      .sort({ createdAt: -1 });
  
      console.log('Found records:', medicalRecords.length);
  
      res.status(200).json({
        success: true,
        medicalRecords
      });
    } catch (error) {
      console.error('Error in getPatientMedicalRecords:', error);
      res.status(500).json({
        success: false,
        message: 'Error mengambil data medical records',
        error: error.message
      });
    }
};

exports.updateMedicalRecord = async (req, res) => {
    try {
        const { patientId, recordId } = req.params;
        const doctorId = req.user.id;
        const updateData = req.body;

        console.log('Debug Info:', {
            patientId,
            recordId,
            doctorId,
            updateData
        });

        // Cari medical record dengan memastikan sesuai dengan doctor dan patient
        const medicalRecord = await MedicalRecord.findOne({
            _id: recordId,
            patient: patientId,
            doctor: doctorId
        });

        if (!medicalRecord) {
            return res.status(404).json({
                success: false,
                message: 'Medical record tidak ditemukan',
                debug: {
                    searchCriteria: {
                        recordId,
                        patientId,
                        doctorId
                    }
                }
            });
        }

        // Update data
        // Pastikan prescription tetap mempertahankan status jika tidak diubah
        if (updateData.prescription) {
            updateData.prescription = updateData.prescription.map((newPrescription, index) => {
                const existingPrescription = medicalRecord.prescription[index];
                return {
                    ...newPrescription,
                    status: existingPrescription ? existingPrescription.status : 'NOT_PURCHASED'
                };
            });
        }

        const updatedRecord = await MedicalRecord.findByIdAndUpdate(
            recordId,
            updateData,
            { 
                new: true,
                runValidators: true
            }
        ).populate('patient', 'name email profile')
         .populate('doctor', 'name email profile');

        res.status(200).json({
            success: true,
            message: 'Medical record berhasil diperbarui',
            medicalRecord: updatedRecord
        });

    } catch (error) {
        console.error('Error in updateMedicalRecord:', error);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan saat mengupdate medical record',
            error: error.message
        });
    }
};

exports.deleteMedicalRecord = async (req, res) => {
    try {
        const { patientId, recordId } = req.params;
        const doctorId = req.user.id;

        // Cari medical record dan pastikan sesuai dengan dokter dan pasien
        const medicalRecord = await MedicalRecord.findOne({
            _id: recordId,
            patient: patientId,
            doctor: doctorId
        });

        if (!medicalRecord) {
            return res.status(404).json({
                success: false,
                message: 'Medical record tidak ditemukan'
            });
        }

        // Hapus medical record
        await MedicalRecord.findByIdAndDelete(recordId);

        res.status(200).json({
            success: true,
            message: 'Medical record berhasil dihapus'
        });

    } catch (error) {
        console.error('Error in deleteMedicalRecord:', error);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan saat menghapus medical record',
            error: error.message
        });
    }
};

exports.updatePrescriptionStatus = async (req, res) => {
    try {
        const { recordId, prescriptionId } = req.params;
        const { status } = req.body;

        const medicalRecord = await MedicalRecord.findById(recordId);

        if (!medicalRecord) {
            return res.status(404).json({
                success: false,
                message: 'Medical record not found'
            });
        }

        // Find and update the specific prescription
        const prescription = medicalRecord.prescription.id(prescriptionId);
        if (!prescription) {
            return res.status(404).json({
                success: false,
                message: 'Prescription not found'
            });
        }

        prescription.status = status;
        await medicalRecord.save();

        res.status(200).json({
            success: true,
            medicalRecord
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error updating prescription status',
            error: error.message
        });
    }
};

exports.getPatientAppointments = async (req, res) => {
    try {
        const { patientId } = req.params;

        // Validasi pasien
        const patient = await User.findOne({ _id: patientId, role: 'patient' });
        if (!patient) {
            return res.status(404).json({
                success: false,
                message: 'Pasien tidak ditemukan'
            });
        }

        const appointments = await Appointment.find({
            doctor: req.user._id,
            patient: patientId
        })
        .populate('patient', 'name email profile')
        .sort({ date: 1 });

        res.status(200).json({
            success: true,
            appointments
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error mengambil data appointments',
            error: error.message
        });
    }
};

exports.getDoctorAppointments = async (req, res) => {
    try {
        const appointments = await Appointment.find({
            doctor: req.user._id
        })
        .populate('patient', 'name email profile')
        .sort({ date: 1 });

        res.status(200).json({
            success: true,
            appointments
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error fetching appointments',
            error: error.message
        });
    }
};

exports.updateAppointmentStatus = async (req, res) => {
    try {
        const { appointmentId } = req.params;
        const { status } = req.body;

        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            return res.status(404).json({
                success: false,
                message: 'Appointment not found'
            });
        }

        // Verify that the doctor owns this appointment
        if (appointment.doctor.toString() !== req.user._id.toString()) {
            return res.status(403).json({
                success: false,
                message: 'Not authorized to update this appointment'
            });
        }

        appointment.status = status;
        await appointment.save();

        res.status(200).json({
            success: true,
            appointment
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error updating appointment status',
            error: error.message
        });
    }
};
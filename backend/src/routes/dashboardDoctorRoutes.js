const express = require('express');
const router = express.Router();
const doctorController = require('../controllers/dashboardDoctorController');
const authMiddleware = require('../middleware/auth.middleware');
const roleMiddleware = require('../middleware/role.middleware');

// Apply middleware to all routes
router.use(authMiddleware);
router.use(roleMiddleware(['doctor']));

// Rute manajemen pasien
router.get('/patients', doctorController.getPatients);

router.get('/medical-records', doctorController.getDoctorMedicalRecords);

// Semua rute di bawah ini menggunakan ID pasien
router.get('/patients/:patientId/history', doctorController.getPatientHistory);

// Rute medical records berdasarkan pasien
router.get('/patients/:patientId/medical-records', doctorController.getPatientMedicalRecords);
router.post('/patients/:patientId/medical-records', doctorController.addMedicine);
router.put('/patients/:patientId/medical-records/:recordId', doctorController.updateMedicalRecord);
router.delete('/patients/:patientId/medical-records/:recordId', doctorController.deleteMedicalRecord);

// Rute prescription berdasarkan pasien
router.put('/patients/:patientId/medical-records/:recordId/prescriptions/:prescriptionId', doctorController.updatePrescriptionStatus);

// Rute untuk melihat semua appointments (overview)
router.get('/appointments', doctorController.getDoctorAppointments);

// Rute appointment berdasarkan pasien
router.get('/patients/:patientId/appointments', doctorController.getPatientAppointments);
router.put('/patients/:patientId/appointments/:appointmentId', doctorController.updateAppointmentStatus);

module.exports = router;
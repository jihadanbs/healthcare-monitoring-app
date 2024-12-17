const express = require('express');
const router = express.Router();
const { getDashboard, addMedicine, scheduleAppointment } = require('../controllers/patientController');
const authMiddleware = require('../middleware/auth.middleware');

router.get('/dashboard', authMiddleware, getDashboard);
router.post('/medicine', authMiddleware, addMedicine);
router.post('/appointment', authMiddleware, scheduleAppointment);

module.exports = router;

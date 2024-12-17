const express = require('express');
const router = express.Router();
const medicationController = require('../controllers/medicationController');
const authMiddleware = require('../middleware/auth.middleware');
const roleMiddleware = require('../middleware/role.middleware');

router.post('/', authMiddleware, roleMiddleware('doctor'), medicationController.addMedication);
router.get('/', authMiddleware, medicationController.getMedications);

module.exports = router;
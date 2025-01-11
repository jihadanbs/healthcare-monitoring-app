const express = require('express');
const router = express.Router();
const dashboardController = require('../controllers/dashboardPatientController');
const authMiddleware = require('../middleware/auth.middleware');

// Route untuk mendapatkan data dashboard
router.get('/dashboard', authMiddleware, dashboardController.getDashboardData);

// Route untuk mendapatkan daftar obat 
router.get('/medicines/prescribed', authMiddleware, dashboardController.getPrescribedMedicines);

// Route untuk memperbarui status pembelian obat
router.post('/medicines/:id/status', authMiddleware, dashboardController.updateMedicineStatus);

// Route untuk mendapatkan daftar obat di checkout
router.get('/checkout/medicines', authMiddleware, dashboardController.getCheckoutMedicines);

// Route untuk update status checkout
router.post('/checkout/status', authMiddleware, dashboardController.updateCheckoutStatus);


module.exports = router;
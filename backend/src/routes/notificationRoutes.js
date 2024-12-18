const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notificationController');
const authMiddleware = require('../middleware/auth.middleware');

// Dapatkan semua notifikasi pengguna
router.get('/notifications', authMiddleware, notificationController.getUserNotifications);

// Tandai notifikasi sebagai sudah dibaca
router.patch('/notifications/:notificationId/read', 
    authMiddleware, 
    notificationController.markNotificationAsRead
);

// Buat notifikasi baru (biasanya digunakan oleh sistem)
router.post('/notifications', 
    authMiddleware, 
    notificationController.createNotification
);

module.exports = router;
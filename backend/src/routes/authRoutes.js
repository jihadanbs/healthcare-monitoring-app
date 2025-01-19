const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/authController');
const authMiddleware = require('../middleware/auth.middleware');
const roleMiddleware = require('../middleware/role.middleware');

// Rute Registrasi
router.post('/register', AuthController.register);

// Rute Login
router.post('/login', AuthController.login);

// Rute Profile (memerlukan autentikasi)
router.get('/profile', authMiddleware, AuthController.getUserProfile);

// Rute Update Profile
router.put('/profile', authMiddleware, AuthController.updateUserProfile);

// Rute Delete User
router.delete('/profile', authMiddleware, AuthController.deleteUser);

// Rute khusus dokter
router.get('/doctor-only', authMiddleware, roleMiddleware(['doctor']), (req, res) => { res.json({ message: 'Ini adalah rute khusus dokter' }); });

module.exports = router;
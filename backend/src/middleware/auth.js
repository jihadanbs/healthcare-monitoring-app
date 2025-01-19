// middleware/auth.js
const jwt = require('jsonwebtoken');
const User = require('../models/User');

exports.protect = async (req, res, next) => {
    try {
        // Log untuk debugging
        console.log('Headers:', req.headers);
        
        let token;
        
        if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
            token = req.headers.authorization.split(' ')[1];
        }
        
        console.log('Token diterima:', token);
        
        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Tidak ada token, akses ditolak'
            });
        }
        
        // Verifikasi token
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        console.log('Token decoded:', decoded);
        
        // Cek apakah user masih ada di database
        const user = await User.findById(decoded.id);
        console.log('User ditemukan:', user);
        
        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'User tidak ditemukan'
            });
        }
        
        // Tambahkan user ke request
        req.user = user;
        next();
        
    } catch (error) {
        console.error('Auth error:', error);
        return res.status(401).json({
            success: false,
            message: 'Token tidak valid atau kadaluarsa'
        });
    }
};

exports.authorizeRoles = (...roles) => {
    return (req, res, next) => {
        console.log('User role:', req.user.role);
        console.log('Allowed roles:', roles);
        
        if (!roles.includes(req.user.role)) {
            return res.status(403).json({
                success: false,
                message: `Role ${req.user.role} tidak diizinkan mengakses resource ini`
            });
        }
        next();
    };
};
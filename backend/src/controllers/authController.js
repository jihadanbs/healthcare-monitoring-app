const User = require('../models/User');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

class AuthController {
    // Registrasi Pengguna
    static async register(req, res) {
        try {
        const { name, email, password, role } = req.body;

        // Cek apakah user sudah ada
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ message: 'Email sudah terdaftar, silakan gunakan email lain!' });
        }

        // Hash password
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // Buat user baru
        const newUser = new User({
            name,
            email,
            password: hashedPassword,
            role
        });

        await newUser.save();

        // Generate token
        const token = jwt.sign(
            { 
            id: newUser._id, 
            role: newUser.role 
            }, 
            process.env.JWT_SECRET, 
            { expiresIn: '1d' }
        );

        res.status(201).json({
            message: 'Registrasi berhasil',
            token,
            user: {
            id: newUser._id,
            name: newUser.name,
            email: newUser.email,
            role: newUser.role
            }
        });
        } catch (error) {
        res.status(500).json({ message: 'Kesalahan server', error: error.message });
        }
    }

    // Login Pengguna
    static async login(req, res) {
        try {
        const { email, password } = req.body;

        // Cari user
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ message: 'Email tidak ditemukan' });
        }

        // Cek password
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: 'Password salah' });
        }

        // Generate token
        const token = jwt.sign(
            { 
            id: user._id, 
            role: user.role 
            }, 
            process.env.JWT_SECRET, 
            { expiresIn: '1d' }
        );

        res.json({
            message: 'Login berhasil',
            token,
            user: {
            id: user._id,
            name: user.name,
            email: user.email,
            role: user.role
            }
        });
        } catch (error) {
        res.status(500).json({ message: 'Kesalahan server', error: error.message });
        }
    }

    // Profile pengguna
    static async getUserProfile(req, res) {
        try {
        // Ambil ID user dari token yang sudah di-decode di middleware
        const user = await User.findById(req.user.id).select('-password');
        
        if (!user) {
            return res.status(404).json({ message: 'User tidak ditemukan' });
        }

        res.json(user);
        } catch (error) {
        res.status(500).json({ message: 'Kesalahan server', error: error.message });
        }
    }

    static async updateUserProfile(req, res) {
        try {
            const { name, email, profile } = req.body;
            
            // Cek apakah email baru sudah digunakan (jika email diubah)
            if (email) {
                const existingUser = await User.findOne({ 
                    email, 
                    _id: { $ne: req.user.id } 
                });
                if (existingUser) {
                    return res.status(400).json({ 
                        message: 'Email sudah digunakan' 
                    });
                }
            }

            const updatedUser = await User.findByIdAndUpdate(
                req.user.id,
                {
                    $set: {
                        name: name,
                        email: email,
                        profile: profile
                    }
                },
                { new: true, runValidators: true }
            ).select('-password');

            if (!updatedUser) {
                return res.status(404).json({ 
                    message: 'User tidak ditemukan' 
                });
            }

            res.json({
                message: 'Profile berhasil diperbarui',
                user: updatedUser
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Kesalahan server', 
                error: error.message 
            });
        }
    }

    static async deleteUser(req, res) {
        try {
            const deletedUser = await User.findByIdAndDelete(req.user.id);
            
            if (!deletedUser) {
                return res.status(404).json({ 
                    message: 'User tidak ditemukan' 
                });
            }

            res.json({ 
                message: 'User berhasil dihapus' 
            });
        } catch (error) {
            res.status(500).json({ 
                message: 'Kesalahan server', 
                error: error.message 
            });
        }
    }
}

module.exports = AuthController;
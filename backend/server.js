const express = require('express');
const cors = require('cors');
const connectDB = require('./src/config/database');
const authRoutes = require('./src/routes/authRoutes');
const dashboardPatientRoutes = require('./src/routes/dashboardPatientRoutes');
const dashboardDoctorRoutes = require('./src/routes/dashboardDoctorRoutes');

// Inisialisasi Express
const app = express();

// Koneksi Database
connectDB();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', authRoutes);
// Routes Pasien
app.use('/api/dashboard', dashboardPatientRoutes);
// Routes Dokter
app.use('/api/dashboard/doctor', dashboardDoctorRoutes);

// Port
const PORT = process.env.PORT || 5000;

// Jalankan Server
app.listen(PORT, () => {
  console.log(`Server berjalan di port ${PORT}`);
});
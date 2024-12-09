const express = require('express');
const cors = require('cors');
const connectDB = require('./src/config/database');
const authRoutes = require('./src/routes/authRoutes');

// Inisialisasi Express
const app = express();

// Koneksi Database
connectDB();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/api/auth', authRoutes);

// Port
const PORT = process.env.PORT || 5000;

// Jalankan Server
app.listen(PORT, () => {
  console.log(`Server berjalan di port ${PORT}`);
});
// const jwt = require('jsonwebtoken');

// const authMiddleware = (req, res, next) => {
//   // Ambil token dari header
//   const token = req.header('Authorization')?.replace('Bearer ', '');

//   if (!token) {
//     return res.status(401).json({ message: 'Tidak ada token, otorisasi ditolak' });
//   }

//   try {
//     // Verifikasi token
//     const decoded = jwt.verify(token, process.env.JWT_SECRET);
//     req.user = decoded;
//     next();
//   } catch (error) {
//     res.status(401).json({ message: 'Token tidak valid' });
//   }
// };

// module.exports = authMiddleware;

const jwt = require('jsonwebtoken');

const authMiddleware = (req, res, next) => {
  try {
    // Log untuk debugging
    console.log('Headers yang diterima:', req.headers);
    
    const token = req.header('Authorization')?.replace('Bearer ', '');
    console.log('Token yang diekstrak:', token);
    
    if (!token) {
      return res.status(401).json({ 
        success: false,
        message: 'Tidak ada token, otorisasi ditolak' 
      });
    }
    
    // Verifikasi token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    console.log('Hasil decode token:', decoded);
    
    // Tambahkan informasi user ke request
    req.user = decoded;
    console.log('User yang ditambahkan ke request:', req.user);
    
    next();
  } catch (error) {
    console.error('Error pada auth middleware:', error);
    res.status(401).json({ 
      success: false,
      message: 'Token tidak valid',
      error: error.message 
    });
  }
};

module.exports = authMiddleware;
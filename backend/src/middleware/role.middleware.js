const roleMiddleware = (roles) => {
    return (req, res, next) => {
        // Pastikan user sudah terautentikasi
        if (!req.user) {
            return res.status(401).json({ message: 'Tidak terautentikasi' });
        }
    
        // Cek apakah role user sesuai
        if (!roles.includes(req.user.role)) {
            return res.status(403).json({ message: 'Akses ditolak' });
        }
        next();
        };
    };
  
  module.exports = roleMiddleware;
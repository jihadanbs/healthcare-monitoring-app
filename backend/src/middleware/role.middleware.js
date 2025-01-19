// const roleMiddleware = (roles) => {
//     return (req, res, next) => {
//         // Pastikan user sudah terautentikasi
//         if (!req.user) {
//             return res.status(401).json({ message: 'Tidak terautentikasi' });
//         }
    
//         // Cek apakah role user sesuai
//         if (!roles.includes(req.user.role)) {
//             return res.status(403).json({ message: 'Akses ditolak' });
//         }
//         next();
//         };
//     };
  
//   module.exports = roleMiddleware;

const roleMiddleware = (roles) => {
    return (req, res, next) => {
      console.log('Role user:', req.user.role);
      console.log('Roles yang diizinkan:', roles);
      
      if (!roles.includes(req.user.role)) {
        return res.status(403).json({
          success: false,
          message: `Role ${req.user.role} tidak memiliki akses ke resource ini`
        });
      }
      
      next();
    };
  };
  
  module.exports = roleMiddleware;
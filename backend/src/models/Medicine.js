const mongoose = require('mongoose');

const medicineSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true 
    },
    status: { 
        type: String, 
        enum: ['belum dibeli', 'sedang dalam proses', 'sudah dibeli'], 
        default: 'belum dibeli' 
    },
    prescribedBy: { 
        type: String, 
        required: true 
    },
    patientId: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'User', 
        required: true 
    },
});

module.exports = mongoose.model('Medicine', medicineSchema);

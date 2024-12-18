const mongoose = require('mongoose');

const ConsultationSchema = new mongoose.Schema({
    patient: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    doctor: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    date: {
        type: Date,
        required: true
    },
    time: {
        type: String,
        required: true
    },
    status: {
        type: String,
        enum: ['scheduled', 'completed', 'cancelled'],
        default: 'scheduled'
    },
    reason: {
        type: String
    },
    notes: {
        type: String
    },
    consultationType: {
        type: String,
        enum: ['online', 'offline'],
        default: 'offline'
    },
    medicalRecordRef: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'MedicalRecord'
    }
}, { 
    timestamps: true 
});

module.exports = mongoose.model('Consultation', ConsultationSchema);
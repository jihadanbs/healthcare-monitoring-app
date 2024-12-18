const mongoose = require('mongoose');

const MedicalRecordSchema = new mongoose.Schema({
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
    consultation: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Consultation'
    },
    diagnosis: {
        type: String,
        required: true
    },
    symptoms: [{
        type: String
    }],
    prescription: [{
        medicine: {
            type: String,
            required: true
        },
        dosage: {
            type: String,
            required: true
        },
        frequency: {
            type: String,
            required: true
        },
        status: {
            type: String,
            enum: ['NOT_PURCHASED', 'IN_PROGRESS', 'PURCHASED'],
            default: 'NOT_PURCHASED'
        }
    }],
    additionalNotes: {
        type: String
    },
    followUpDate: {
        type: Date
    }
}, { 
    timestamps: true 
});

module.exports = mongoose.model('MedicalRecord', MedicalRecordSchema);
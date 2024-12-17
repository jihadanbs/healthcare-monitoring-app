const mongoose = require('mongoose');

const HealthRecordSchema = new mongoose.Schema({
    patientId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    visitDate: { type: Date, required: true },
    diagnosis: { type: String, required: true },
    notes: { type: String }
});

module.exports = mongoose.model('HealthRecord', HealthRecordSchema);

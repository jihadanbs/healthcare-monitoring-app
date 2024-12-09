const mongoose = require('mongoose');

const MedicalRecordSchema = new mongoose.Schema({
  patient: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  doctor: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  healthMetrics: {
    heartRate: Number,
    bloodPressure: {
      systolic: Number,
      diastolic: Number
    },
    bloodSugar: Number,
    weight: Number,
    height: Number
  },
  diagnosis: String,
  prescription: [String],
  notes: String
}, { timestamps: true });

module.exports = mongoose.model('MedicalRecord', MedicalRecordSchema);
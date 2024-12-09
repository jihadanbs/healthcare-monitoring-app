const mongoose = require('mongoose');

const NotificationSchema = new mongoose.Schema({
  recipient: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  type: {
    type: String,
    enum: ['consultation', 'medical_record', 'system', 'reminder'],
    required: true
  },
  content: String,
  isRead: {
    type: Boolean,
    default: false
  },
  relatedDocument: {
    type: mongoose.Schema.Types.ObjectId,
    refPath: 'relatedModel'
  },
  relatedModel: {
    type: String,
    enum: ['Consultation', 'MedicalRecord']
  }
}, { timestamps: true });

module.exports = mongoose.model('Notification', NotificationSchema);
const mongoose = require('mongoose');

const NotificationSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    title: {
        type: String,
        required: true
    },
    message: {
        type: String,
        required: true
    },
    type: {
        type: String,
        enum: ['appointment_reminder', 'medical_record', 'prescription', 'general'],
        default: 'general'
    },
    relatedEntity: {
        entityType: {
            type: String,
            enum: ['consultation', 'medical_record', 'prescription', null],
            default: null
        },
        entityId: {
            type: mongoose.Schema.Types.ObjectId,
            default: null
        }
    },
    isRead: {
        type: Boolean,
        default: false
    },
    priority: {
        type: String,
        enum: ['low', 'medium', 'high'],
        default: 'low'
    }
}, { 
    timestamps: true 
});

module.exports = mongoose.model('Notification', NotificationSchema);
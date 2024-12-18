const Notification = require('../models/Notification');

exports.getUserNotifications = async (req, res) => {
    try {
        const userId = req.user.id;
        const notifications = await Notification.find({ 
            user: userId 
        }).sort({ createdAt: -1 });

        res.json(notifications);
    } catch (error) {
        res.status(500).json({ 
            message: 'Error fetching notifications', 
            error: error.message 
        });
    }
};

exports.markNotificationAsRead = async (req, res) => {
    try {
        const { notificationId } = req.params;
        const notification = await Notification.findByIdAndUpdate(
            notificationId, 
            { isRead: true }, 
            { new: true }
        );

        if (!notification) {
            return res.status(404).json({ message: 'Notification not found' });
        }

        res.json(notification);
    } catch (error) {
        res.status(500).json({ 
            message: 'Error marking notification as read', 
            error: error.message 
        });
    }
};

exports.createNotification = async (req, res) => {
    try {
        const { 
            user, 
            title, 
            message, 
            type, 
            relatedEntity,
            priority 
        } = req.body;

        const notification = new Notification({
            user,
            title,
            message,
            type,
            relatedEntity,
            priority
        });

        await notification.save();

        res.status(201).json(notification);
    } catch (error) {
        res.status(500).json({ 
            message: 'Error creating notification', 
            error: error.message 
        });
    }
};
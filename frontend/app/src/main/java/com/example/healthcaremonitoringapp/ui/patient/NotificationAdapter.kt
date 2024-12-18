package com.example.healthcaremonitoringapp.ui.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.models.NotificationType
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private val onNotificationClickListener: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view, onNotificationClickListener)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationViewHolder(
        itemView: View,
        private val onNotificationClickListener: (Notification) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.notificationTitleTextView)
        private val messageTextView: TextView = itemView.findViewById(R.id.notificationMessageTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.notificationDateTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.notificationTypeTextView)

        fun bind(notification: Notification) {
            titleTextView.text = notification.title
            messageTextView.text = notification.message

            // Format date
            val dateFormatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            dateTextView.text = dateFormatter.format(notification.date)

            // Set notification type
            typeTextView.text = when(notification.type) {
                NotificationType.APPOINTMENT_REMINDER -> "Pengingat Janji"
                NotificationType.MEDICAL_RECORD -> "Rekam Medis"
                NotificationType.PRESCRIPTION -> "Resep Obat"
                NotificationType.GENERAL -> "Umum"
            }

            // Set read/unread state
            itemView.setBackgroundColor(
                itemView.context.getColor(
                    if (notification.isRead)
                        android.R.color.transparent
                    else
                        android.R.color.darker_gray
                )
            )

            // Handle click to mark as read
            itemView.setOnClickListener {
                onNotificationClickListener(notification)
            }
        }
    }

    // DiffUtil for efficient list updates
    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}
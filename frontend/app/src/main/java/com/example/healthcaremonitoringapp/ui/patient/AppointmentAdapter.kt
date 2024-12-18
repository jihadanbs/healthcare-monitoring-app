package com.example.healthcaremonitoringapp.ui.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Appointment
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentAdapter :
    ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        private val specialityTextView: TextView = itemView.findViewById(R.id.specialityTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)

        fun bind(appointment: Appointment) {
            doctorNameTextView.text = appointment.doctorName
            specialityTextView.text = appointment.speciality

            // Format date
            val dateFormatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            dateTextView.text = dateFormatter.format(appointment.date)

            // Set status
            statusTextView.text = appointment.status.toString()
        }
    }

    // DiffUtil for efficient list updates
    class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }
    }
}
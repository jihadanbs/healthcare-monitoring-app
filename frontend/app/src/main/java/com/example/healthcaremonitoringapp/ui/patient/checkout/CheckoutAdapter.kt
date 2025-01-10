package com.example.healthcaremonitoringapp.ui.patient.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine

class CheckoutAdapter(
    private val onRemoveClick: (Medicine) -> Unit
) : ListAdapter<Medicine, CheckoutAdapter.CheckoutViewHolder>(CheckoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckoutViewHolder(
        itemView: View,
        private val onRemoveClick: (Medicine) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.checkoutMedicineNameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.checkoutPriceTextView)

        fun bind(medicine: Medicine) {
            nameTextView.text = medicine.medicine
            priceTextView.text = "Rp ${medicine.price}"

            itemView.findViewById<View>(R.id.removeButton).setOnClickListener {
                onRemoveClick(medicine)
            }
        }
    }

    class CheckoutDiffCallback : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem == newItem
        }
    }
}
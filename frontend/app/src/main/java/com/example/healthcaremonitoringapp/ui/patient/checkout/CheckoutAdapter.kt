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

    class CheckoutViewHolder(
        itemView: View,
        private val onRemoveClick: (Medicine) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.checkoutMedicineNameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.checkoutPriceTextView)
        private val dosageTextView: TextView = itemView.findViewById(R.id.checkoutDosageTextView)
        private val frequencyTextView: TextView = itemView.findViewById(R.id.checkoutFrequencyTextView)
        private val removeButton: View = itemView.findViewById(R.id.removeButton)

        fun bind(medicine: Medicine) {
            nameTextView.text = medicine.medicine
            dosageTextView.text = "Dosis: ${medicine.dosage}"
            frequencyTextView.text = "Frekuensi: ${medicine.frequency}"
            priceTextView.text = "Harga: Rp. ${medicine.price}"

            removeButton.setOnClickListener {
                onRemoveClick(medicine)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view, onRemoveClick)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
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
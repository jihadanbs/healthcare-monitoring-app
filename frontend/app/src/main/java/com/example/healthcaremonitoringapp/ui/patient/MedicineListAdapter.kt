package com.example.healthcaremonitoringapp.ui.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.PurchaseStatus

class MedicineListAdapter(
    private val onStatusChangeListener: (Medicine, PurchaseStatus) -> Unit
) : ListAdapter<Medicine, MedicineListAdapter.MedicineViewHolder>(MedicineDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine, parent, false)
        return MedicineViewHolder(view, onStatusChangeListener)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MedicineViewHolder(
        itemView: View,
        private val onStatusChangeListener: (Medicine, PurchaseStatus) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)
        private val dosageTextView: TextView = itemView.findViewById(R.id.dosageTextView)
        private val frequencyTextView: TextView = itemView.findViewById(R.id.frequencyTextView)
        private val statusTextView: TextView = itemView.findViewById(R.id.purchaseStatusTextView)
        private val statusButton: Button = itemView.findViewById(R.id.changeStatusButton)

        fun bind(medicine: Medicine) {
            nameTextView.text = medicine.medicine
            dosageTextView.text = "Dosis: ${medicine.dosage}"
            frequencyTextView.text = "Frekuensi: ${medicine.frequency}"

            // Set purchase status text and color
            when (medicine.status) {
                PurchaseStatus.NOT_PURCHASED -> {
                    statusTextView.text = "Belum Dibeli"
                    statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                    statusButton.text = "Mulai Proses Beli"
                    statusButton.setOnClickListener {
                        onStatusChangeListener(medicine, PurchaseStatus.IN_PROGRESS)
                    }
                }
                PurchaseStatus.IN_PROGRESS -> {
                    statusTextView.text = "Sedang Diproses"
                    statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                    statusButton.text = "Tandai Sudah Dibeli"
                    statusButton.setOnClickListener {
                        onStatusChangeListener(medicine, PurchaseStatus.PURCHASED)
                    }
                }
                PurchaseStatus.PURCHASED -> {
                    statusTextView.text = "Sudah Dibeli"
                    statusTextView.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    statusButton.isEnabled = false
                    statusButton.text = "Selesai"
                }
            }

            statusButton.setOnClickListener {
                try {
                    when (medicine.status) {
                        PurchaseStatus.NOT_PURCHASED -> {
                            Toast.makeText(
                                itemView.context,
                                "Memulai proses pembelian obat ${medicine.medicine}",
                                Toast.LENGTH_SHORT
                            ).show()
                            onStatusChangeListener(medicine, PurchaseStatus.IN_PROGRESS)
                        }
                        PurchaseStatus.IN_PROGRESS -> {
                            Toast.makeText(
                                itemView.context,
                                "Menandai obat ${medicine.medicine} sudah dibeli",
                                Toast.LENGTH_SHORT
                            ).show()
                            onStatusChangeListener(medicine, PurchaseStatus.PURCHASED)
                        }
                        PurchaseStatus.PURCHASED -> {
                            // Button sudah disabled untuk status ini
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        itemView.context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // DiffUtil for efficient list updates
    class MedicineDiffCallback : DiffUtil.ItemCallback<Medicine>() {
        override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
            return oldItem == newItem
        }
    }
}
package com.example.ajikapps.list

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ajikapps.R
import com.example.ajikapps.database.SuratRequestEntity
import com.example.ajikapps.databinding.ItemRequestListBinding

class RequestListAdapter(
    private var items: List<SuratRequestEntity>,
    private val onDeleteClick: (SuratRequestEntity) -> Unit
) : RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<SuratRequestEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class RequestViewHolder(private val binding: ItemRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuratRequestEntity) {
            binding.tvServiceTitle.text = item.serviceTitle
            binding.tvDate.text = item.date
            binding.tvApplicantName.text = item.fullName
            binding.tvApplicantNik.text = item.nik
            binding.tvPurpose.text = item.purpose
            binding.tvStatus.text = item.status

            // Bind icon resource based on service ID
            val iconResId = when (item.serviceId) {
                1 -> R.drawable.ic_domisili
                2 -> R.drawable.ic_usaha
                3 -> R.drawable.ic_skck
                4 -> R.drawable.ic_tidak_mampu
                5 -> R.drawable.ic_kelahiran
                6 -> R.drawable.ic_kematian
                7 -> R.drawable.ic_pindah
                8 -> R.drawable.ic_nikah
                else -> R.drawable.ic_mail
            }
            binding.ivIcon.setImageResource(iconResId)

            // Dynamic Styling for Status Badge
            if (item.status.equals("Selesai", ignoreCase = true)) {
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9")) // Light Green
                binding.tvStatus.setTextColor(Color.parseColor("#2E7D32")) // Dark Green
            } else {
                // Default style: Diproses (Teal)
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCFBF1")) // Light Teal
                binding.tvStatus.setTextColor(Color.parseColor("#0F766E")) // Dark Teal
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}

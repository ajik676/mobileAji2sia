package com.example.ajikapps.surat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ajikapps.databinding.ItemSuratGridBinding

class SuratGridAdapter(
    private val items: List<SuratModel>,
    private val onItemClick: (SuratModel) -> Unit
) : RecyclerView.Adapter<SuratGridAdapter.SuratViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuratViewHolder {
        val binding = ItemSuratGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuratViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuratViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SuratViewHolder(private val binding: ItemSuratGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SuratModel) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.ivIcon.setImageResource(item.iconRes)
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}

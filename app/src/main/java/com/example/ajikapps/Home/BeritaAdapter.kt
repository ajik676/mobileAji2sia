package com.example.ajikapps.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ajikapps.R
import com.example.ajikapps.databinding.ItemBeritaBinding

class BeritaAdapter(
    private var items: List<NewsModel>,
    private val onItemClick: (NewsModel) -> Unit
) : RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val binding = ItemBeritaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeritaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<NewsModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class BeritaViewHolder(private val binding: ItemBeritaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NewsModel) {
            binding.tvNewsTitle.text = item.title
            binding.tvNewsDate.text = item.date
            binding.tvNewsDescription.text = item.description

            // Load Image using Glide with fade transition
            Glide.with(itemView.context)
                .load(item.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_news)
                .error(R.drawable.ic_news)
                .into(binding.ivNewsImage)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}

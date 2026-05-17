package com.example.ajikapps.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.ajikapps.R

class MessageAdapter(
    private val context: Context,
    private val dataSource: List<MessageModel>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size
    override fun getItem(position: Int): Any = dataSource[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Inflate layout item_message.xml
        val rowView = convertView ?: inflater.inflate(R.layout.item_message, parent, false)

        // Hubungkan dengan ID di item_message.xml
        val imgAvatar = rowView.findViewById<ImageView>(R.id.imgAvatar)
        val tvName = rowView.findViewById<TextView>(R.id.tvName)
        val tvMessage = rowView.findViewById<TextView>(R.id.tvMessage)

        // Ambil data berdasarkan posisi
        val message = getItem(position) as MessageModel

        // Set teks
        tvName.text = message.name
        tvMessage.text = message.message

        // Gunakan Glide untuk memuat gambar dari URL
        Glide.with(context)
            .load(message.imageUrl)
            .circleCrop()
            .placeholder(R.mipmap.ic_launcher_round) // Gambar sementara saat proses loading
            .into(imgAvatar)

        return rowView
    }
}
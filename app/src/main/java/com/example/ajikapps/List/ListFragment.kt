package com.example.ajikapps.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ajikapps.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menggunakan ViewBinding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 5. Definisikan list data message (Saya sertakan dummy URL gambar avatar)
        val messageList = listOf(
            MessageModel("Alya", "Halo! Apa kabar?", "https://i.pravatar.cc/150?img=1"),
            MessageModel("Budi", "Sudah dikerjakan?", "https://i.pravatar.cc/150?img=11"),
            MessageModel("Citra", "Jangan lupa tugasnya ya!", "https://i.pravatar.cc/150?img=5"),
            MessageModel("Dika", "Besok kita rapat jam 9.", "https://i.pravatar.cc/150?img=12"),
            MessageModel("Eka", "Nice job kemarin!", "https://i.pravatar.cc/150?img=9")
        )

        // 6 & 7. Buat Adapter lalu terapkan ke ListView di XML
        val adapter = MessageAdapter(requireContext(), messageList)
        binding.listView.adapter = adapter

        // 8. Terapkan OnClick pada setiap item ListView
        binding.listView.setOnItemClickListener { parent, view, position, id ->
            val selectedMessage = messageList[position]
            Toast.makeText(
                requireContext(),
                "Membuka pesan dari ${selectedMessage.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak
    }
}
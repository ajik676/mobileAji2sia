package com.example.ajikapps.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ajikapps.AuthActivity
import com.example.ajikapps.R
import com.example.ajikapps.databinding.FragmentHomeBinding
import android.content.Context.MODE_PRIVATE
import com.example.ajikapps.Home.pertemuan4.FourthActivity
import com.example.ajikapps.Home.pertemuan7.Sevenctivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = requireContext().getSharedPreferences("user_pref", MODE_PRIVATE)
        binding.btnToFourth.setOnClickListener {
            val intent = Intent(requireContext(), FourthActivity::class.java)

            intent.putExtra("name", "Politeknik Caltex Riau")
            intent.putExtra("from", "Rumbai")
            intent.putExtra("age", 25)

            startActivity(intent)
        }
        binding.btnToseven.setOnClickListener {
            val intent = Intent(requireContext(), Sevenctivity::class.java)
            startActivity(intent)
        }
        binding.Logout.setOnClickListener{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin melanjutkan?")
                .setPositiveButton("Ya") { dialog, _ ->
                    dialog.dismiss()
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    Log.e("Info Dialog","Anda memilih Ya!")
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog","Anda memilih Tidak!")
                    requireActivity().finish()
                }
                .show()
        }
    }
}


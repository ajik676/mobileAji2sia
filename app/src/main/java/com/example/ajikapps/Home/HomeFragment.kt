package com.example.ajikapps.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ajikapps.SplashScreenActivity
import com.example.ajikapps.databinding.FragmentHomeBinding
import com.example.ajikapps.home.pertemuan2.SecondActivity
import com.example.ajikapps.pertemuan_5.FifthActivity
import com.example.ajikapps.home.pertemuan7.Sevenctivity
import com.example.ajikapps.home.pertemuan_9.NinthActivity
import com.example.ajikapps.home.pertemuan_10.TenthActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(username: String): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            bundle.putString("username", username)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences(
            "user_pref",
            android.content.Context.MODE_PRIVATE
        )

        // Pertemuan 2
        binding.btnPertemuan2.setOnClickListener {
            startActivity(Intent(requireContext(), SecondActivity::class.java))
        }

        // Pertemuan 3 (Contoh jika belum ada activity, tampilkan toast)
        binding.btnPertemuan3.setOnClickListener {
             Toast.makeText(requireContext(), "Halaman Pertemuan 3 belum tersedia", Toast.LENGTH_SHORT).show()
        }

        // Pertemuan 4 (Contoh jika belum ada activity, tampilkan toast)
        binding.btnPertemuan4.setOnClickListener {
            Toast.makeText(requireContext(), "Halaman Pertemuan 4 belum tersedia", Toast.LENGTH_SHORT).show()
        }

        // Pertemuan 5
        binding.btnPertemuan5.setOnClickListener {
            startActivity(Intent(requireContext(), FifthActivity::class.java))
        }

        // Pertemuan 7
        binding.btnPertemuan7.setOnClickListener {
            startActivity(Intent(requireContext(), Sevenctivity::class.java))
        }

        // Pertemuan 9
        binding.btnPertemuan9.setOnClickListener {
            startActivity(Intent(requireContext(), NinthActivity::class.java))
        }

        // Pertemuan 10
        binding.btnPertemuan10.setOnClickListener {
            startActivity(Intent(requireContext(), TenthActivity::class.java))
        }

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Yakin ingin logout?")
                .setPositiveButton("Ya") { dialog, _ ->
                    sharedPref.edit().clear().apply()
                    dialog.dismiss()

                    val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
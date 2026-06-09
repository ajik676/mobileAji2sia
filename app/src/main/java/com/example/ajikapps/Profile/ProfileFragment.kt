package com.example.ajikapps.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ajikapps.AuthActivity
import com.example.ajikapps.databinding.FragmentProfileBinding
import com.example.ajikapps.utils.PreferenceManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Profil Saya"

        // Ambil data dari shared preferences
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Warga Desa") ?: "Warga Desa"

        // Set nama pengguna secara dinamis (huruf kapital)
        binding.tvName.text = username.uppercase()

        // Set detail profil statis/mockup kependudukan
        binding.tvNik.text = "3276051212990003"
        binding.tvPhone.text = "+62 812-3456-7890"
        binding.tvAddress.text = "Jl. Merdeka No. 12, RT 01/RW 02, Desa Bina Karya"

        // Logout listener
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Keluar")
                .setMessage("Yakin ingin keluar dari akun Anda?")
                .setPositiveButton("Ya") { dialog, _ ->
                    // Hapus sesi user dan reset status onboarding
                    sharedPref.edit().clear().apply()
                    PreferenceManager(requireContext()).setOnboardingCompleted(false)
                    dialog.dismiss()

                    // Pindah ke halaman AuthActivity/Login
                    val intent = Intent(requireContext(), AuthActivity::class.java)
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
package com.example.ajikapps.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.ajikapps.SplashScreenActivity
import com.example.ajikapps.databinding.FragmentHomeBinding

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
            Context.MODE_PRIVATE
        )

        // 1. Mengambil username dari bundle, default "Warga Desa" jika kosong
        val username = arguments?.getString("username") ?: "Warga Desa"

        // 2. Mengubah teks pada TextView tvUsername agar menyapa user di Header
        binding.tvUsername.text = username

        /* * CATATAN UNTUK MENU BARU:
         * binding.btnWebsite dihapus karena di XML baru sudah diganti dengan GridLayout.
         * Jika Anda ingin membuat menu "Buat Surat" atau "Info Desa" bisa diklik
         * dan membuka WebView, Anda harus menambahkan ID di LinearLayout menu tersebut
         * di dalam XML (contoh: android:id="@+id/menuBuatSurat"), lalu panggil di sini:
         * * binding.menuBuatSurat.setOnClickListener {
         * startActivity(Intent(requireContext(), BinaDesaWebView::class.java))
         * }
         */

        // 3. Tombol Logout (Tetap menggunakan btnLogout dari XML baru)
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Keluar")
                .setMessage("Yakin ingin keluar dari akun Anda?")
                .setPositiveButton("Ya") { dialog, _ ->

                    // Hapus sesi user
                    sharedPref.edit().clear().apply()
                    dialog.dismiss()

                    // Pindah ke halaman Splash/Login
                    val intent = Intent(
                        requireContext(),
                        SplashScreenActivity::class.java
                    )
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
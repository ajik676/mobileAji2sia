package com.example.ajikapps.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajikapps.R
import com.example.ajikapps.SplashScreenActivity
import com.example.ajikapps.data.local.AppDatabase
import com.example.ajikapps.data.local.SuratRequestEntity
import com.example.ajikapps.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var beritaAdapter: BeritaAdapter

    // Permission result launcher for scanner
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startQrScanner()
        } else {
            Toast.makeText(requireContext(), "Izin kamera dibutuhkan untuk memindai QR Code", Toast.LENGTH_SHORT).show()
        }
    }

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

        // 3. Setup RecyclerView untuk Berita
        setupNewsRecyclerView()

        // 4. Setup ViewModel untuk Berita
        setupNewsViewModel()

        // 5. Tombol Refresh/Coba Lagi Berita
        binding.btnRefreshNews.setOnClickListener {
            newsViewModel.fetchNews()
        }

        // 6. Tombol Logout
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

        // 7. QR Scan floating action button
        binding.fabScanQR.setOnClickListener {
            checkCameraPermissionAndStartScan()
        }
    }

    private fun setupNewsRecyclerView() {
        beritaAdapter = BeritaAdapter(emptyList()) { news ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(news.title)
                .setMessage("${news.date}\n\n${news.description}")
                .setPositiveButton("Tutup") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.rvBeritaNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }
    }

    private fun setupNewsViewModel() {
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        newsViewModel.newsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewsState.Loading -> {
                    binding.progressBarNews.visibility = View.VISIBLE
                    binding.rvBeritaNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.GONE
                }
                is NewsState.Success -> {
                    binding.progressBarNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.GONE
                    binding.rvBeritaNews.visibility = View.VISIBLE
                    beritaAdapter.updateData(state.news)
                }
                is NewsState.Error -> {
                    binding.progressBarNews.visibility = View.GONE
                    binding.rvBeritaNews.visibility = View.GONE
                    binding.layoutEmptyStateNews.visibility = View.VISIBLE
                    binding.tvEmptyStateMessageNews.text = state.message
                }
            }
        }

        // Ambil berita saat awal dimuat
        newsViewModel.fetchNews()
    }

    private fun checkCameraPermissionAndStartScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startQrScanner()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan QR Code Verifikasi Surat")
        integrator.setCameraId(0) // Gunakan kamera belakang
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setCaptureActivity(com.journeyapps.barcodescanner.CaptureActivity::class.java)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Pemindaian dibatalkan", Toast.LENGTH_SHORT).show()
            } else {
                verifyScannedSurat(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun verifyScannedSurat(contents: String) {
        if (contents.startsWith("ajikapps://verify?id=")) {
            val idStr = contents.substringAfter("id=")
            val id = idStr.toIntOrNull()
            if (id != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(requireContext())
                    val request = db.suratDao().getRequestById(id)

                    withContext(Dispatchers.Main) {
                        if (request != null) {
                            showVerificationSuccessDialog(request)
                        } else {
                            showVerificationErrorDialog("Dokumen dengan ID #$id tidak ditemukan dalam sistem database desa.")
                        }
                    }
                }
            } else {
                showVerificationErrorDialog("Kode QR tidak valid atau korup.")
            }
        } else {
            showVerificationErrorDialog("QR Code bukan format Surat Desa yang valid.")
        }
    }

    private fun showVerificationSuccessDialog(request: SuratRequestEntity) {
        val details = """
            Status: VALID & TERVERIFIKASI (OK)
            
            • Jenis Surat: ${request.serviceTitle}
            • Pemohon: ${request.fullName.uppercase()}
            • NIK: ${request.nik}
            • No Telepon: ${request.phone}
            • Keperluan: ${request.purpose}
            • Tanggal Pengajuan: ${request.date}
            • Status Saat Ini: ${request.status}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hasil Verifikasi: VALID")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setMessage(details)
            .setPositiveButton("Selesai") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showVerificationErrorDialog(errorMessage: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hasil Verifikasi: GAGAL")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(errorMessage)
            .setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.ajikapps.surat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ajikapps.R
import com.example.ajikapps.data.local.AppDatabase
import com.example.ajikapps.data.local.SuratRequestEntity
import com.example.ajikapps.data.local.SuratServiceEntity
import com.example.ajikapps.databinding.DialogApplySuratBinding
import com.example.ajikapps.databinding.FragmentSuratBinding
import com.example.ajikapps.notification.NotificationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SuratFragment : Fragment() {

    private var _binding: FragmentSuratBinding? = null
    private val binding get() = _binding!!

    private var capturedPhotoPath: String? = null
    private var currentDialogBinding: DialogApplySuratBinding? = null

    // Camera Result Launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                saveBitmapToCache(bitmap)
            } else {
                Toast.makeText(requireContext(), "Gagal memproses foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Permission Launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Notification Permission Launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuratBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadServicesFromDb()
    }

    private fun loadServicesFromDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            var services = db.suratDao().getAllServices()
            
            // Handle first-launch latency in prepopulation
            if (services.isEmpty()) {
                delay(300)
                services = db.suratDao().getAllServices()
            }
            
            withContext(Dispatchers.Main) {
                if (isAdded) {
                    setupRecyclerView(services)
                }
            }
        }
    }

    private fun setupRecyclerView(services: List<SuratServiceEntity>) {
        val listSurat = services.map { entity ->
            val iconResId = requireContext().resources.getIdentifier(
                entity.iconName,
                "drawable",
                requireContext().packageName
            )
            SuratModel(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                iconRes = if (iconResId != 0) iconResId else R.drawable.ic_mail,
                requirements = entity.requirements
            )
        }

        val suratAdapter = SuratGridAdapter(listSurat) { surat ->
            showDetailDialog(surat)
        }

        binding.rvSurat.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = suratAdapter
        }
    }

    private fun showDetailDialog(surat: SuratModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(surat.iconRes)
            .setTitle("Syarat ${surat.title}")
            .setMessage("Berikut adalah berkas persyaratan yang wajib disiapkan:\n\n${surat.requirements}")
            .setPositiveButton("Ajukan Sekarang") { _, _ ->
                showApplicationForm(surat)
            }
            .setNegativeButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showApplicationForm(surat: SuratModel) {
        val dialogBinding = DialogApplySuratBinding.inflate(layoutInflater)
        currentDialogBinding = dialogBinding
        capturedPhotoPath = null // Reset photo path for new form

        dialogBinding.tvFormTitle.text = "Form Pengajuan\n${surat.title}"

        // Load default values from preferences and mock details
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val defaultUsername = sharedPref.getString("username", "") ?: ""
        
        dialogBinding.etNama.setText(defaultUsername.uppercase())
        dialogBinding.etNik.setText("3276051212990003") // Mock default NIK
        dialogBinding.etPhone.setText("+62 812-3456-7890") // Mock default phone

        // Setup capture photo button
        dialogBinding.btnCapturePhoto.setOnClickListener {
            checkPermissionAndLaunchCamera()
        }

        // Request notification permission on Android 13+
        checkNotificationPermission()

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Kirim Pengajuan", null)
            .setNegativeButton("Batal") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()

        dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val nik = dialogBinding.etNik.text.toString().trim()
            val nama = dialogBinding.etNama.text.toString().trim()
            val phone = dialogBinding.etPhone.text.toString().trim()
            val keperluan = dialogBinding.etKeperluan.text.toString().trim()

            if (nik.length < 16) {
                dialogBinding.etNik.error = "NIK harus 16 digit"
                return@setOnClickListener
            }
            if (nama.isEmpty()) {
                dialogBinding.etNama.error = "Nama wajib diisi"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                dialogBinding.etPhone.error = "Nomor telepon wajib diisi"
                return@setOnClickListener
            }
            if (keperluan.isEmpty()) {
                dialogBinding.etKeperluan.error = "Keperluan wajib diisi"
                return@setOnClickListener
            }
            if (capturedPhotoPath == null) {
                Toast.makeText(requireContext(), "Harap ambil foto KTP/KK terlebih dahulu!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Save to Room Database
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val request = SuratRequestEntity(
                serviceId = surat.id,
                serviceTitle = surat.title,
                username = defaultUsername,
                nik = nik,
                fullName = nama,
                phone = phone,
                purpose = keperluan,
                status = "Diproses",
                date = currentDate,
                documentPhotoPath = capturedPhotoPath
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                db.suratDao().insertRequest(request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Pengajuan ${surat.title} Berhasil Dikirim!",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Trigger immediate success notification
                    triggerInstantNotification(surat.title)
                    
                    dialog.dismiss()
                }
            }
        }
    }

    private fun checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun saveBitmapToCache(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cacheDir = requireContext().cacheDir
                val file = File(cacheDir, "document_${System.currentTimeMillis()}.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                fos.flush()
                fos.close()

                capturedPhotoPath = file.absolutePath

                withContext(Dispatchers.Main) {
                    currentDialogBinding?.let { binding ->
                        binding.cardPhotoPreview.visibility = View.VISIBLE
                        binding.ivPhotoPreview.setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }

    private fun triggerInstantNotification(letterTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationHelper.showInstantNotification(
                    requireContext(),
                    "📝 Pengajuan Surat Terkirim",
                    "Pengajuan untuk $letterTitle telah sukses dikirim ke Balai Desa."
                )
            }
        } else {
            NotificationHelper.showInstantNotification(
                requireContext(),
                "📝 Pengajuan Surat Terkirim",
                "Pengajuan untuk $letterTitle telah sukses dikirim ke Balai Desa."
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentDialogBinding = null
        _binding = null
    }
}

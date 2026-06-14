package com.example.ajikapps.surat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ajikapps.R
import com.example.ajikapps.database.AppDatabase
import com.example.ajikapps.database.SuratRequestEntity
import com.example.ajikapps.database.SuratServiceEntity
import com.example.ajikapps.databinding.DialogApplySuratBinding
import com.example.ajikapps.databinding.FragmentSuratBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SuratFragment : Fragment() {

    private var _binding: FragmentSuratBinding? = null
    private val binding get() = _binding!!

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
        dialogBinding.tvFormTitle.text = "Form Pengajuan\n${surat.title}"

        // Load default values from preferences and mock details
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val defaultUsername = sharedPref.getString("username", "") ?: ""
        
        dialogBinding.etNama.setText(defaultUsername.uppercase())
        dialogBinding.etNik.setText("3276051212990003") // Mock default NIK
        dialogBinding.etPhone.setText("+62 812-3456-7890") // Mock default phone

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
                date = currentDate
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
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

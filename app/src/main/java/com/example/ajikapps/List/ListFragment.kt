package com.example.ajikapps.list

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ajikapps.R
import com.example.ajikapps.data.local.AppDatabase
import com.example.ajikapps.data.local.SuratRequestEntity
import com.example.ajikapps.databinding.FragmentListBinding
import com.example.ajikapps.databinding.DialogDetailRequestBinding
import com.example.ajikapps.qr.QrCodeHelper
import com.example.ajikapps.notification.ReminderManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RequestListAdapter
    private var allRequests: List<SuratRequestEntity> = emptyList()
    private var filteredRequests: List<SuratRequestEntity> = emptyList()

    private var currentSearchQuery = ""
    private var currentFilterStatus = "Semua"

    private var pendingReminderLetterTitle: String? = null

    // Permission result launcher for post notifications
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pendingReminderLetterTitle?.let { showDurationSelector(it) }
        } else {
            Toast.makeText(
                requireContext(),
                "Izin notifikasi ditolak. Pengingat tidak dapat diaktifkan.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeDatabase()
    }

    private fun setupRecyclerView() {
        adapter = RequestListAdapter(
            emptyList(),
            onDeleteClick = { request ->
                showDeleteConfirmationDialog(request)
            },
            onItemClick = { request ->
                showRequestDetailDialog(request)
            }
        )
        binding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequests.adapter = adapter
    }

    private fun setupListeners() {
        // Search Listener
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                filterData()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Chip Filter Listener
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilterStatus = when (checkedIds.firstOrNull()) {
                R.id.chipPending -> "Diproses"
                R.id.chipSuccess -> "Selesai"
                else -> "Semua"
            }
            filterData()
        }
    }

    private fun observeDatabase() {
        val sharedPref = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "") ?: ""

        val db = AppDatabase.getDatabase(requireContext())
        // Observe requests for the logged in user
        db.suratDao().getRequestsByUsername(username).observe(viewLifecycleOwner) { requests ->
            allRequests = requests
            filterData()
        }
    }

    private fun filterData() {
        filteredRequests = allRequests.filter { request ->
            // Filter by Status Chip
            val matchesStatus = if (currentFilterStatus == "Semua") {
                true
            } else {
                request.status.equals(currentFilterStatus, ignoreCase = true)
            }

            // Filter by Search Query (matches applicant name or service title)
            val matchesSearch = request.fullName.contains(currentSearchQuery, ignoreCase = true) ||
                    request.serviceTitle.contains(currentSearchQuery, ignoreCase = true)

            matchesStatus && matchesSearch
        }

        adapter.updateData(filteredRequests)

        // Show empty state if list is empty
        if (filteredRequests.isEmpty()) {
            binding.rvRequests.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        } else {
            binding.rvRequests.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmationDialog(request: SuratRequestEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Batalkan Pengajuan")
            .setMessage("Apakah Anda yakin ingin membatalkan pengajuan ${request.serviceTitle} ini?")
            .setPositiveButton("Ya, Batalkan") { dialog, _ ->
                deleteRequestFromDb(request)
                dialog.dismiss()
            }
            .setNegativeButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteRequestFromDb(request: SuratRequestEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            db.suratDao().deleteRequest(request)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "Pengajuan berhasil dibatalkan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showRequestDetailDialog(request: SuratRequestEntity) {
        val dialogBinding = DialogDetailRequestBinding.inflate(layoutInflater)
        
        dialogBinding.tvDetailTitle.text = request.serviceTitle
        dialogBinding.tvDetailStatus.text = request.status
        dialogBinding.tvDetailNik.text = request.nik
        dialogBinding.tvDetailNama.text = request.fullName.uppercase()
        dialogBinding.tvDetailPhone.text = request.phone
        dialogBinding.tvDetailKeperluan.text = request.purpose
        dialogBinding.tvDetailDate.text = request.date

        // Status pill styling
        if (request.status.equals("Selesai", ignoreCase = true)) {
            dialogBinding.tvDetailStatus.setTextColor(resources.getColor(R.color.primary, null))
        }

        // Load photo if exists
        if (!request.documentPhotoPath.isNullOrEmpty()) {
            val imgFile = File(request.documentPhotoPath)
            if (imgFile.exists()) {
                dialogBinding.cardDetailPhoto.visibility = View.VISIBLE
                dialogBinding.tvLabelPhoto.visibility = View.VISIBLE
                Glide.with(this)
                    .load(imgFile)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(dialogBinding.ivDetailPhoto)
            } else {
                dialogBinding.cardDetailPhoto.visibility = View.GONE
                dialogBinding.tvLabelPhoto.visibility = View.GONE
            }
        } else {
            dialogBinding.cardDetailPhoto.visibility = View.GONE
            dialogBinding.tvLabelPhoto.visibility = View.GONE
        }

        // Generate QR Code containing deep link
        val qrContent = "ajikapps://verify?id=${request.id}"
        val qrBitmap = QrCodeHelper.generateQrCode(qrContent)
        if (qrBitmap != null) {
            dialogBinding.ivDetailQr.setImageBitmap(qrBitmap)
        }

        // Setup reminder button click listener
        dialogBinding.btnSetReminder.setOnClickListener {
            checkNotificationPermissionAndShowSelector(request.serviceTitle)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setPositiveButton("Tutup") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun checkNotificationPermissionAndShowSelector(letterTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                showDurationSelector(letterTitle)
            } else {
                pendingReminderLetterTitle = letterTitle
                requestNotificationPermissionLauncher.launch(permission)
            }
        } else {
            showDurationSelector(letterTitle)
        }
    }

    private fun showDurationSelector(letterTitle: String) {
        val options = arrayOf("10 Detik (Untuk Uji Coba)", "1 Jam", "1 Hari")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih Waktu Pengingat")
            .setItems(options) { dialog, which ->
                val delaySeconds = when (which) {
                    0 -> 10L // 10 seconds
                    1 -> 3600L // 1 hour
                    2 -> 86400L // 1 day
                    else -> 10L
                }

                ReminderManager.scheduleReminder(
                    requireContext(),
                    letterTitle,
                    delaySeconds
                )

                Toast.makeText(
                    requireContext(),
                    "Pengingat status '$letterTitle' disetel (${options[which]})",
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
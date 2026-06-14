package com.example.ajikapps.list

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ajikapps.R
import com.example.ajikapps.database.AppDatabase
import com.example.ajikapps.database.SuratRequestEntity
import com.example.ajikapps.databinding.FragmentListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RequestListAdapter
    private var allRequests: List<SuratRequestEntity> = emptyList()
    private var filteredRequests: List<SuratRequestEntity> = emptyList()

    private var currentSearchQuery = ""
    private var currentFilterStatus = "Semua"

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
        adapter = RequestListAdapter(emptyList()) { request ->
            showDeleteConfirmationDialog(request)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
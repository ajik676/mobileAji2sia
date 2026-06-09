package com.example.ajikapps.surat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ajikapps.R
import com.example.ajikapps.databinding.FragmentSuratBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val listSurat = listOf(
            SuratModel(
                1,
                "Surat Keterangan Domisili",
                "Keterangan domisili penduduk.",
                R.drawable.ic_domisili,
                "• Kartu Tanda Penduduk (KTP)\n• Kartu Keluarga (KK)\n• Surat pengantar dari RT/RW setempat"
            ),
            SuratModel(
                2,
                "Surat Keterangan Usaha",
                "Keterangan kepemilikan usaha.",
                R.drawable.ic_usaha,
                "• KTP & KK Pemohon\n• Surat Pengantar RT/RW\n• Foto Tempat/Objek Usaha\n• Surat pernyataan kepemilikan usaha"
            ),
            SuratModel(
                3,
                "Surat Pengantar SKCK",
                "Pengantar pembuatan SKCK.",
                R.drawable.ic_skck,
                "• KTP & KK Pemohon\n• Surat Pengantar RT/RW\n• Pas foto 4x6 latar belakang merah (2 lembar)"
            ),
            SuratModel(
                4,
                "Surat Keterangan Tidak Mampu",
                "Keterangan kondisi tidak mampu.",
                R.drawable.ic_tidak_mampu,
                "• KTP & KK Pemohon\n• Surat Pengantar RT/RW\n• Surat pernyataan tidak mampu bermaterai\n• Bukti slip gaji/keterangan penghasilan RT"
            ),
            SuratModel(
                5,
                "Surat Kelahiran",
                "Pencatatan kelahiran baru.",
                R.drawable.ic_kelahiran,
                "• KTP Suami & Istri\n• Kartu Keluarga (KK)\n• Surat keterangan lahir dari bidan/rumah sakit\n• KTP 2 orang saksi kelahiran"
            ),
            SuratModel(
                6,
                "Surat Kematian",
                "Pencatatan kematian warga.",
                R.drawable.ic_kematian,
                "• KTP & KK jenazah\n• KTP pelapor (ahli waris)\n• Surat keterangan kematian dari rumah sakit/RT setempat"
            ),
            SuratModel(
                7,
                "Surat Pindah Penduduk",
                "Keterangan pindah domisili.",
                R.drawable.ic_pindah,
                "• Kartu Keluarga (KK) asli\n• KTP pemohon asli\n• Alamat lengkap daerah tujuan pindah\n• Pas foto 3x4 (3 lembar)"
            ),
            SuratModel(
                8,
                "Surat Pengantar Nikah",
                "Pengantar nikah KUA.",
                R.drawable.ic_nikah,
                "• KTP & KK calon mempelai\n• KTP orang tua kandung\n• Akta kelahiran calon mempelai\n• Surat Pengantar RT/RW\n• Pas foto 2x3 latar biru (4 lembar)"
            )
        )

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

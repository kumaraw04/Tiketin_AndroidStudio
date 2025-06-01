package com.azhar.pemesanantiket.view.input

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.azhar.pemesanantiket.R
import com.azhar.pemesanantiket.databinding.ActivityInputDataBinding
import com.azhar.pemesanantiket.viewmodel.InputDataViewModel
import java.text.SimpleDateFormat
import java.util.*

class DataKeretaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputDataBinding
    private lateinit var inputDataViewModel: InputDataViewModel

    private val strAsal = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strTujuan = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strKelas = arrayOf("Eksekutif", "Bisnis", "Ekonomi")

    private lateinit var sAsal: String
    private lateinit var sTujuan: String
    private lateinit var sTanggal: String
    private lateinit var sNama: String
    private lateinit var sTelp: String
    private lateinit var sKelas: String

    private var hargaDewasa = 0
    private var hargaAnak = 0
    private var hargaKelas = 0
    private var hargaTotalDewasa = 0
    private var hargaTotalAnak = 0
    private var hargaTotal = 0
    private var countAnak = 0
    private var countDewasa = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()
        setToolbar()
        setViewModel()
        setSpinnerAdapter()
        setJmlPenumpang()
        setInputData()
    }

    private fun setViewModel() {
        inputDataViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(InputDataViewModel::class.java)
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setSpinnerAdapter() {
        val adapterAsal = ArrayAdapter(this, android.R.layout.simple_spinner_item, strAsal)
        adapterAsal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBerangkat.adapter = adapterAsal

        val adapterTujuan = ArrayAdapter(this, android.R.layout.simple_spinner_item, strTujuan)
        adapterTujuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spTujuan.adapter = adapterTujuan

        val adapterKelas = ArrayAdapter(this, android.R.layout.simple_spinner_item, strKelas)
        adapterKelas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spKelas.adapter = adapterKelas

        binding.spBerangkat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sAsal = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spTujuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sTujuan = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spKelas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sKelas = parent.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setJmlPenumpang() {
        binding.imageAdd1.setOnClickListener {
            countAnak++
            binding.tvJmlAnak.text = countAnak.toString()
        }
        binding.imageMinus1.setOnClickListener {
            if (countAnak > 0) {
                countAnak--
                binding.tvJmlAnak.text = countAnak.toString()
            }
        }
        binding.imageAdd2.setOnClickListener {
            countDewasa++
            binding.tvJmlDewasa.text = countDewasa.toString()
        }
        binding.imageMinus2.setOnClickListener {
            if (countDewasa > 0) {
                countDewasa--
                binding.tvJmlDewasa.text = countDewasa.toString()
            }
        }
    }

    private fun setInputData() {
        binding.btnCheckout.setOnClickListener {
            // Ambil data dari input
            sNama = binding.inputNama.text.toString().trim()
            sTanggal = binding.inputTanggal.text.toString().trim()
            sTelp = binding.inputTelepon.text.toString().trim()

            // Debugging Log
            Log.d("DEBUG", "Nama: $sNama, Tanggal: $sTanggal, Telp: $sTelp, Asal: $sAsal, Tujuan: $sTujuan")

            // Validasi Asal dan Tujuan harus berbeda
            if (sAsal == sTujuan) {
                Toast.makeText(this, "Asal dan Tujuan tidak boleh sama!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validasi input tidak boleh kosong dan jumlah dewasa minimal 1
            if (sNama.isEmpty() || sTanggal.isEmpty() || sTelp.isEmpty() || countDewasa <= 0) {
                Toast.makeText(this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan data ke ViewModel
            inputDataViewModel.addDataPemesan(
                sNama, sAsal, sTujuan, sTanggal, sTelp,
                countAnak, countDewasa, hargaTotal, sKelas, "1"
            )

            // Tampilkan pesan sukses dan tutup activity
            Toast.makeText(this, "Booking Tiket berhasil, cek di menu riwayat", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) layoutParams.flags = layoutParams.flags or bits
            else layoutParams.flags = layoutParams.flags and bits.inv()
            window.attributes = layoutParams
        }
    }
}

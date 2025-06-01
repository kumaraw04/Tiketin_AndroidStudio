package com.azhar.pemesanantiket.view.input

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.azhar.pemesanantiket.R
import com.azhar.pemesanantiket.databinding.ActivityInputDataBinding
import com.azhar.pemesanantiket.viewmodel.InputDataViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class DataKapalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputDataBinding
    private lateinit var inputDataViewModel: InputDataViewModel
    val uid = FirebaseAuth.getInstance().currentUser?.uid


    private val strAsal = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strTujuan = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strKelas = arrayOf("Eksekutif", "Bisnis", "Ekonomi")

    private var sAsal: String = strAsal[0]
    private var sTujuan: String = strTujuan[0]
    private var sTanggal: String = ""
    private var sNama: String = ""
    private var sTelp: String = ""
    private var sKelas: String = strKelas[0]

    private var countAnak = 0
    private var countDewasa = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()
        setToolbar()
        setInitView()
        setViewModel()
        setSpinnerAdapter()
        setJmlPenumpang()
        setInputData()
    }

    private fun setViewModel() {
        inputDataViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[InputDataViewModel::class.java]
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setInitView() {
        binding.inputTanggal.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val tanggalJemput = Calendar.getInstance()
                val date = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    tanggalJemput.set(year, month, dayOfMonth)
                    val format = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                    setText(format.format(tanggalJemput.time))
                }
                DatePickerDialog(
                    this@DataKapalActivity, date,
                    tanggalJemput[Calendar.YEAR],
                    tanggalJemput[Calendar.MONTH],
                    tanggalJemput[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }
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
            if (countAnak > 0) countAnak--
            binding.tvJmlAnak.text = countAnak.toString()
        }

        binding.imageAdd2.setOnClickListener {
            countDewasa++
            binding.tvJmlDewasa.text = countDewasa.toString()
        }
        binding.imageMinus2.setOnClickListener {
            if (countDewasa > 0) countDewasa--
            binding.tvJmlDewasa.text = countDewasa.toString()
        }
    }

    private fun setInputData() {
        binding.btnCheckout.setOnClickListener {
            sNama = binding.inputNama.text.toString().trim()
            sTanggal = binding.inputTanggal.text.toString().trim()
            sTelp = binding.inputTelepon.text.toString().trim()

            if (sNama.isEmpty() || sTanggal.isEmpty() || sTelp.isEmpty() || countDewasa == 0) {
                Toast.makeText(this, "Mohon lengkapi data pemesanan!", Toast.LENGTH_SHORT).show()
            } else if (sAsal == sTujuan) {
                Toast.makeText(this, "Asal dan Tujuan tidak boleh sama!", Toast.LENGTH_LONG).show()
            } else {
                // Hitung harga berdasarkan kelas
                val hargaDewasa: Int
                val hargaAnak: Int

                when (sKelas) {
                    "Eksekutif" -> {
                        hargaDewasa = 150000
                        hargaAnak = 75000
                    }
                    "Bisnis" -> {
                        hargaDewasa = 100000
                        hargaAnak = 50000
                    }
                    else -> { // Ekonomi
                        hargaDewasa = 70000
                        hargaAnak = 35000
                    }
                }

                val totalHarga = (countDewasa * hargaDewasa) + (countAnak * hargaAnak)

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    inputDataViewModel.addDataPemesan(
                        uid,
                        sNama,
                        sAsal,
                        sTujuan,
                        sTanggal,
                        sTelp,
                        countAnak,
                        countDewasa,
                        totalHarga,
                        sKelas,
                        "1"
                    )
                    Toast.makeText(this, "Booking Tiket berhasil, cek di menu riwayat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "User belum login!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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

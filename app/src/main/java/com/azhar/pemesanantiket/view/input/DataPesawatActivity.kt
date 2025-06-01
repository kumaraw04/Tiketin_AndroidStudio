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
import java.util.*

class DataPesawatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputDataBinding
    private lateinit var inputDataViewModel: InputDataViewModel

    private val strAsal = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strTujuan = arrayOf("Jakarta", "Semarang", "Surabaya", "Bali")
    private val strKelas = arrayOf("Eksekutif", "Bisnis", "Ekonomi")

    private var hargaDewasa = 0
    private var hargaAnak = 0
    private var hargaKelas = 0
    private var hargaTotal = 0
    private var countAnak = 0
    private var countDewasa = 0
    private var sAsal = ""
    private var sTujuan = ""
    private var sTanggal = ""
    private var sNama = ""
    private var sTelp = ""
    private var sKelas = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()
        setToolbar()
        setViewModel()
        setSpinnerAdapter()
        setJmlPenumpang()
        setDatePicker()
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
        setupSpinner(binding.spBerangkat, strAsal) { sAsal = it }
        setupSpinner(binding.spTujuan, strTujuan) { sTujuan = it }
        setupSpinner(binding.spKelas, strKelas) { sKelas = it }
    }

    private fun setupSpinner(spinner: Spinner, items: Array<String>, onItemSelected: (String) -> Unit) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected(items[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setJmlPenumpang() {
        setupCounter(binding.imageAdd1, binding.imageMinus1, binding.tvJmlAnak) { countAnak = it }
        setupCounter(binding.imageAdd2, binding.imageMinus2, binding.tvJmlDewasa) { countDewasa = it }
    }

    private fun setupCounter(plus: View, minus: View, textView: TextView, onUpdate: (Int) -> Unit) {
        var count = 0
        plus.setOnClickListener {
            count++
            textView.text = count.toString()
            onUpdate(count)
        }
        minus.setOnClickListener {
            if (count > 0) count--
            textView.text = count.toString()
            onUpdate(count)
        }
    }

    private fun setDatePicker() {
        binding.inputTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                sTanggal = "$dayOfMonth/${month + 1}/$year"
                binding.inputTanggal.setText(sTanggal)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun hitungTotalHarga() {
        hargaDewasa = countDewasa * 500000
        hargaAnak = countAnak * 300000
        hargaKelas = when (sKelas) {
            "Eksekutif" -> 200000
            "Bisnis" -> 100000
            "Ekonomi" -> 0
            else -> 0
        }
        hargaTotal = hargaDewasa + hargaAnak + hargaKelas
        Log.d("DEBUG", "Harga Total: $hargaTotal")
    }

    private fun setInputData() {
        binding.btnCheckout.setOnClickListener {
            sNama = binding.inputNama.text.toString().trim()
            sTelp = binding.inputTelepon.text.toString().trim()
            hitungTotalHarga()

            if (sAsal == sTujuan) {
                showToast("Asal dan Tujuan tidak boleh sama!")
                return@setOnClickListener
            }

            if (sNama.isEmpty() || sTanggal.isEmpty() || sTelp.isEmpty() || countDewasa <= 0) {
                showToast("Mohon lengkapi data pemesanan!")
                return@setOnClickListener
            }

            inputDataViewModel.addDataPemesan(
                sNama, sAsal, sTujuan, sTanggal, sTelp,
                countAnak, countDewasa, hargaTotal, sKelas, "1"
            )

            showToast("Booking Tiket berhasil!")
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}
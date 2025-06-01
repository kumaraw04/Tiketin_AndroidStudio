package com.azhar.pemesanantiket

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.azhar.pemesanantiket.databinding.ActivityMainBinding
import com.azhar.pemesanantiket.view.history.HistoryActivity
import com.azhar.pemesanantiket.Account
import com.azhar.pemesanantiket.view.input.DataKapalActivity
import com.azhar.pemesanantiket.view.input.DataKeretaActivity
import com.azhar.pemesanantiket.view.input.DataPesawatActivity
import com.azhar.pemesanantiket.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class Main : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Navigasi ke halaman lain
        binding.imageProfile.setOnClickListener {
            startActivity(Intent(this, Account::class.java))
        }

        binding.cvPesawat.setOnClickListener {
            startActivity(Intent(this, DataPesawatActivity::class.java))
        }

        binding.cvKapal.setOnClickListener {
            startActivity(Intent(this, DataKapalActivity::class.java))
        }

        binding.cvKereta.setOnClickListener {
            startActivity(Intent(this, DataKeretaActivity::class.java))
        }
    }

    private fun logoutUser() {
        auth.signOut() // Logout Firebase

        // Hapus status login dari SharedPreferences
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        // Arahkan ke SignInActivity
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish() // Tutup MainActivity
    }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
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

package com.azhar.pemesanantiket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.pemesanantiket.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import android.text.InputType
import com.azhar.pemesanantiket.view.history.HistoryActivity


class Account : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth: FirebaseAuth

    // Simulasi password asli (sebaiknya tidak disimpan di client)
    private val realPassword = "userActualPassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // Jika user terdaftar, tampilkan email dan password (dengan bintang)
        user?.let {
            binding.tvEmail.text = it.email
            binding.tvPassword.setText("********")  // default sembunyikan password
        } ?: run {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
        }


        // Tombol untuk mengubah password
        binding.btnChangePassword.setOnClickListener {
            val email = user?.email
            if (email != null) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Link reset password dikirim ke email.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Gagal mengirim email reset.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Tombol verifikasi email
        binding.btnVerifyEmail.setOnClickListener {
            user?.let {
                if (!it.isEmailVerified) {
                    it.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Link verifikasi dikirim ke email.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Email sudah diverifikasi.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, Main::class.java))
            finish()
        }
        binding.btnDeleteAccount.setOnClickListener {
            user?.let {
                it.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Akun berhasil dihapus.", Toast.LENGTH_LONG).show()
                        logoutUser()
                    } else {
                        Toast.makeText(this, "Gagal menghapus akun: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(this, "Anda telah logout.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}


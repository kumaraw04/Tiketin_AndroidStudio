package com.azhar.pemesanantiket.view.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.azhar.pemesanantiket.Main
import com.azhar.pemesanantiket.R
import com.azhar.pemesanantiket.SignInActivity
import com.azhar.pemesanantiket.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Deklarasi variabel
    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set OnClickListener untuk tombol
        binding.btnSignOut.setOnClickListener(this)
        binding.btnEmailVerify.setOnClickListener(this)
        binding.btnskip.setOnClickListener(this) // Tambahkan tombol Skip
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Reload user untuk mendapatkan status terbaru
            currentUser.reload().addOnCompleteListener {
                updateUI(auth.currentUser)
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnSignOut -> signOut()
            R.id.btnEmailVerify -> sendEmailVerification()
            R.id.btnskip -> goToMainActivity() // Tambahkan aksi untuk btnSkip
        }
    }

    // Fungsi untuk logout
    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Fungsi untuk mengirim email verifikasi
    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    "Verification email sent to ${user.email}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk memperbarui UI dengan informasi pengguna
    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            binding.tvName.text = it.displayName ?: "No Name"
            binding.tvUserId.text = it.email ?: "No Email"

            it.reload().addOnCompleteListener {
                if (user.isEmailVerified) {
                    binding.btnEmailVerify.visibility = View.GONE
                    Toast.makeText(this, "Email sudah terverifikasi", Toast.LENGTH_SHORT).show()
                } else {
                    binding.btnEmailVerify.visibility = View.VISIBLE
                    Toast.makeText(this, "Email belum terverifikasi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk pindah ke MainActivity
    private fun goToMainActivity() {
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
        finish()
    }
}

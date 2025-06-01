package com.azhar.pemesanantiket.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.azhar.pemesanantiket.database.DatabaseClient.Companion.getInstance
import com.azhar.pemesanantiket.database.dao.DatabaseDao
import com.azhar.pemesanantiket.model.ModelDatabase
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class InputDataViewModel(application: Application) : AndroidViewModel(application) {

    fun addDataPemesan(
        uid: String,
        nama_penumpang: String, keberangkatan: String,
        tujuan: String, tanggal: String, nomor_telepon: String,
        anak_anak: Int, dewasa: Int, harga_tiket: Int, kelas: String, status: String
    ) {
        val data = hashMapOf(
            "namaPenumpang" to nama_penumpang,
            "keberangkatan" to keberangkatan,
            "tujuan" to tujuan,
            "tanggal" to tanggal,
            "nomorTelepon" to nomor_telepon,
            "anakAnak" to anak_anak,
            "dewasa" to dewasa,
            "hargaTiket" to harga_tiket,
            "kelas" to kelas,
            "status" to status
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(uid)
            .collection("pemesanan")
            .add(data)
    }

    fun deleteDataPemesan(uid: String, docId: String) {
        FirebaseFirestore.getInstance()
            .collection("dataPemesan")
            .document(uid)
            .collection("tiket")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Log.d("DELETE", "Data berhasil dihapus")
            }
            .addOnFailureListener {
                Log.e("DELETE", "Gagal menghapus data: ${it.message}")
            }
    }

}
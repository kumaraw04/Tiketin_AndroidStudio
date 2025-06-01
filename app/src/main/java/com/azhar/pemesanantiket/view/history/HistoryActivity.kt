package com.azhar.pemesanantiket.view.history

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azhar.pemesanantiket.databinding.ActivityHistoryBinding
import com.azhar.pemesanantiket.model.ModelDatabase
import com.azhar.pemesanantiket.viewmodel.HistoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels()
    private val modelDatabaseList: MutableList<ModelDatabase> = ArrayList()
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menggunakan View Binding
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()
        setToolbar()
        setInitLayout()
        getDataFromFirestore()
        setUpItemTouchHelper()
    }

    private fun getDataHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dataList = ArrayList<ModelDatabase>()

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("pemesanan")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val data = document.toObject(ModelDatabase::class.java)
                    data.docId = document.id
                    dataList.add(data)
                }

                historyAdapter.setDataAdapter(dataList)

                if (dataList.isEmpty()) {
                    binding.tvNotFound.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.tvNotFound.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data dari Firestore", Toast.LENGTH_SHORT).show()
            }
    }



    private fun getDataFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("pemesanan")
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<ModelDatabase>()
                for (doc in documents) {
                    val item = doc.toObject(ModelDatabase::class.java)
                    item.docId = doc.id  // ← WAJIB! Untuk simpan ID dokumen Firestore
                    listData.add(item)
                }

                if (listData.isEmpty()) {
                    binding.tvNotFound.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.tvNotFound.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                    historyAdapter.setDataAdapter(listData)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data dari Firestore", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setInitLayout() {
        historyAdapter = HistoryAdapter(modelDatabaseList)
        binding.rvHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    private fun setViewModel() {
        historyViewModel.dataList.observe(this) { modelDatabases ->
            if (modelDatabases.isEmpty()) {
                binding.tvNotFound.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvNotFound.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
            }
            historyAdapter.setDataAdapter(modelDatabases)
        }
    }

    private fun setUpItemTouchHelper() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val modelDatabase = historyAdapter.setSwipeRemove(swipedPosition)

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val docId = modelDatabase.docId

                if (uid != null && docId != null) {
                    FirebaseFirestore.getInstance()
                        .collection("users") // ✅ ganti dari "dataPemesan"
                        .document(uid)
                        .collection("pemesanan") // ✅ ganti dari "tiket"
                        .document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@HistoryActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@HistoryActivity, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this@HistoryActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvHistory)
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
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}

package com.azhar.pemesanantiket.view.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azhar.pemesanantiket.databinding.ListItemHistoryBinding
import com.azhar.pemesanantiket.model.ModelDatabase
import com.azhar.pemesanantiket.utils.FunctionHelper.rupiahFormat

class HistoryAdapter(private var modelDatabase: MutableList<ModelDatabase>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    fun setDataAdapter(items: List<ModelDatabase>) {
        modelDatabase.clear()
        modelDatabase.addAll(items)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = modelDatabase[position]

        holder.binding.apply {
            tvKode1.text = getKodeBandara(data.keberangkatan)
            tvKode2.text = getKodeBandara(data.tujuan)
            tvKelas.text = data.kelas
            tvDate.text = data.tanggal
            tvNama.text = data.namaPenumpang
            tvKeberangkatan.text = data.keberangkatan
            tvTujuan.text = data.tujuan
            tvHargaTiket.text = rupiahFormat(data.hargaTiket)
        }
    }

    override fun getItemCount(): Int {
        return modelDatabase.size
    }

    inner class ViewHolder(val binding: ListItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    fun setSwipeRemove(position: Int): ModelDatabase {
        val removedItem = modelDatabase.removeAt(position)
        notifyItemRemoved(position)
        return removedItem
    }

    private fun getKodeBandara(kota: String): String {
        return when (kota) {
            "Jakarta" -> "JKT"
            "Semarang" -> "SRG"
            "Surabaya" -> "SUB"
            "Bali" -> "DPS"
            else -> kota
        }
    }
}

package com.example.debtmanagerapp.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.debtmanagerapp.R
import com.example.debtmanagerapp.database.DebtEntity

class DebtAdapter(private var debtList: List<DebtEntity>,
                  private val onItemClicked: (DebtEntity) -> Unit) : RecyclerView.Adapter<DebtAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val creditorTextView: TextView = itemView.findViewById(R.id.creditorTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        // 他のビューをここで取得
    }

    fun updateData(newDebtList: List<DebtEntity>) {
        debtList = newDebtList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debt = debtList[position]

        // データをビューにバインド
        holder.creditorTextView.text = "借入先: ${debt.creditor}"
        holder.amountTextView.text = "借入金額: ${debt.amount}"
        // 他のデータをビューにバインド

        holder.itemView.setOnClickListener {
            onItemClicked(debt)
        }

    }

    override fun getItemCount(): Int {
        return debtList.size
    }
}

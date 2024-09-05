package com.example.budgetingapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetingapp.room.Transaction

class TransactionAdapter(private var transaction: List<Transaction>): RecyclerView.Adapter<TransactionAdapter.ListViewHolder>() {

    class ListViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val label: TextView = view.findViewById(R.id.label)
            val amount: TextView = view.findViewById(R.id.price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_transaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val transaction = transaction[position]
        val context: Context = holder.amount.context

        if(transaction.amount >= 0){
            holder.amount.text = "+ Rp %.3f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        }else{
            holder.amount.text = "- Rp %.3f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = transaction.label

        holder.itemView.setOnClickListener {
            val i = Intent(context, DetailTransaction::class.java)
            i.putExtra("transaction", transaction)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int = transaction.size

    fun setData(transaction: List<Transaction>){
        this.transaction = transaction
        notifyDataSetChanged()
    }
}
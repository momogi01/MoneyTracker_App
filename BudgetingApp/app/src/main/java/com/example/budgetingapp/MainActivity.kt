package com.example.budgetingapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.budgetingapp.databinding.ActivityMainBinding
import com.example.budgetingapp.room.Transaction
import com.example.budgetingapp.room.TransactionDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var oldTransaction: List<Transaction>
    private lateinit var transaction: List<Transaction>
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var room: TransactionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transaction = arrayListOf()

        adapter = TransactionAdapter(transaction)
        binding.rvSpending.layoutManager = LinearLayoutManager(this)
        binding.rvSpending.setHasFixedSize(true)
        binding.rvSpending.adapter = adapter

        room = Room.databaseBuilder(this,
            TransactionDatabase::class.java,
            "transaction").build()

        // swipe untuk hapus data
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transaction[viewHolder.adapterPosition])
            }
        }

        val swipe = ItemTouchHelper(itemTouchHelper)
        swipe.attachToRecyclerView(binding.rvSpending)
        
        binding.addBtn.setOnClickListener {
            val i = Intent(this, AddTransactionActivity::class.java)
            startActivity(i)
        }
    }

    private fun fetchAll(){
        GlobalScope.launch {

            transaction = room.transactionDao().getAll()

            runOnUiThread {
                updateList()
                adapter.setData(transaction)
            }
        }
    }

    private fun showSnackBar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Transaction deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun updateList(){
        val totalAmount : Double = transaction.map { it.amount }.sum()
        val budgetAmount : Double = transaction.filter { it.amount > 0}.map { it.amount }.sum()
        val expenseAmount : Double = totalAmount-budgetAmount

        binding.apply {
            totalBalance.text = "Rp %.3f".format(totalAmount)
            budget.text = "Rp %.3f".format(budgetAmount)
            expense.text = "Rp %.3f".format(expenseAmount)
        }
    }

    private fun deleteTransaction(transactions: Transaction){
        deletedTransaction = transactions
        oldTransaction = transaction

        GlobalScope.launch {
            room.transactionDao().delete(transactions)

            transaction = transaction.filter { it.id != transactions.id }
            runOnUiThread {
                updateList()
                adapter.setData(transaction)
                showSnackBar()
            }
        }
    }

    private fun undoDelete(){
        GlobalScope.launch {
            room.transactionDao().insertAll(deletedTransaction)
            transaction = oldTransaction

            runOnUiThread {
                adapter.setData(transaction)
                updateList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}
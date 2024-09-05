package com.example.budgetingapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.budgetingapp.databinding.ActivityDetailTransactionBinding
import com.example.budgetingapp.room.Transaction
import com.example.budgetingapp.room.TransactionDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailTransaction : AppCompatActivity() {
    private lateinit var transaction: Transaction
    private lateinit var binding: ActivityDetailTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var transaction = intent.getSerializableExtra("transaction") as Transaction

        binding.apply {
            labelInput.setText(transaction.label)
            priceInput.setText(transaction.amount.toString())
            descriptionInput.setText(transaction.desc)

            rootView.setOnClickListener {
                window.decorView.clearFocus()

                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(it.windowToken, 0)
            }

            labelInput.addTextChangedListener {
                updateTransaction.visibility = View.VISIBLE
                if (it!!.count() > 0)
                    labelLayout.error = null
            }

            priceInput.addTextChangedListener {
                updateTransaction.visibility = View.VISIBLE
                if (it!!.count() > 0)
                    priceLayout.error = null
            }

            descriptionInput.addTextChangedListener {
                updateTransaction.visibility = View.VISIBLE
            }

            updateTransaction.setOnClickListener {
                val label = labelInput.text.toString()
                val amount = priceInput.text.toString().toDoubleOrNull()
                val desc = descriptionInput.text.toString()

                if (label.isEmpty()){
                    labelLayout.error = "Enter label"
                }

                else if (amount == null){
                    priceLayout.error = "Enter price"
                }

                else {
                    val transaction = Transaction(transaction.id, label, amount, desc)
                    update(transaction)
                }
            }

            closeBtn.setOnClickListener {
                val i = Intent(this@DetailTransaction, MainActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun update(transaction: Transaction){
        val room = Room.databaseBuilder(this,
            TransactionDatabase::class.java,
            "transaction").build()

        GlobalScope.launch {
            room.transactionDao().update(transaction)
            finish()
        }
    }
}
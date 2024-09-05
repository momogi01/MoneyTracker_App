package com.example.budgetingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.budgetingapp.databinding.ActivityAddTransactionBinding
import com.example.budgetingapp.room.Transaction
import com.example.budgetingapp.room.TransactionDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            labelInput.addTextChangedListener {
                if (it!!.count() > 0)
                    labelLayout.error = null
            }

            priceInput.addTextChangedListener {
                if (it!!.count() > 0)
                    priceLayout.error = null
            }

            addTransaction.setOnClickListener {
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
                    val transaction = Transaction(0, label, amount, desc)
                    insert(transaction)
                }
            }

            closeBtn.setOnClickListener {
                val i = Intent(this@AddTransactionActivity, MainActivity::class.java)
                startActivity(i)
            }
        }
    }
    private fun insert(transaction: Transaction){
        val room = Room.databaseBuilder(this,
            TransactionDatabase::class.java,
            "transaction").build()

        GlobalScope.launch {
            room.transactionDao().insertAll(transaction)
            finish()
        }
    }
}
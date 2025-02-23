package com.example.budgetingapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Transaction::class),
    version = 1)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao() : TransactionDao
}
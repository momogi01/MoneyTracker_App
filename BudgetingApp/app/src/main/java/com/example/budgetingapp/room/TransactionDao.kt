package com.example.budgetingapp.room

import androidx.room.*

@Dao
interface TransactionDao {
    @Query("Select * from `transaction`")
    fun getAll(): List<Transaction>

    @Insert
    fun insertAll(vararg transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(vararg transaction: Transaction)
}
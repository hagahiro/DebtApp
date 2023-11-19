package com.example.debtmanagerapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DebtDao {
    @Insert
    suspend fun insert(debt: DebtEntity)

    @Query("SELECT * FROM DebtEntity")
    suspend fun getAllDebts(): List<DebtEntity>

    @Query("SELECT * FROM debtentity")
    fun getAllDebtsLiveData(): LiveData<List<DebtEntity>>

    @Update
    suspend fun update(debt: DebtEntity)
    @Delete
    suspend fun delete(debt: DebtEntity)
}
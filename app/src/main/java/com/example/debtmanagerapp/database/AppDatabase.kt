package com.example.debtmanagerapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DebtEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun debtDao(): DebtDao

}
package com.azhar.pemesanantiket.database

import androidx.room.Database
import com.azhar.pemesanantiket.model.ModelDatabase
import androidx.room.RoomDatabase
import com.azhar.pemesanantiket.database.dao.DatabaseDao


@Database(entities = [ModelDatabase::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}
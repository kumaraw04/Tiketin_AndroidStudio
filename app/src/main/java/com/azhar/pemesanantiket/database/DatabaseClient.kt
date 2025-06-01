package com.azhar.pemesanantiket.database

import android.content.Context
import androidx.room.Room


class DatabaseClient private constructor(context: Context) {
    var appDatabase: AppDatabase

    companion object {
        @Volatile
        private var mInstance: DatabaseClient? = null

        @Synchronized
        fun getInstance(context: Context): DatabaseClient {
            return mInstance ?: synchronized(this) {
                mInstance ?: DatabaseClient(context).also { mInstance = it }
            }
        }
    }

    init {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "travel_db")
            .fallbackToDestructiveMigration()
            .build()
    }
}

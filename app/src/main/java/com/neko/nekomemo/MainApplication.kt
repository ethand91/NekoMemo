package com.neko.nekomemo

import android.app.Application
import androidx.room.Room
import com.neko.nekomemo.db.MemoDatabase

class MainApplication : Application() {
    companion object {
        lateinit var database: MemoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            MemoDatabase::class.java,
            MemoDatabase.NAME
        )
            .build()
    }
}
package com.neko.nekomemo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Memo::class], version = 2, exportSchema = false)
@TypeConverters(DateTimeConverter::class)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object {
        const val NAME = "memos"
    }
}
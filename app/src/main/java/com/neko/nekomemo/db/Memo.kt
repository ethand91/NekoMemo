package com.neko.nekomemo.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Entity(tableName = MemoDatabase.NAME)
data class Memo(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    var title: String = "",
    var body: String = "",
    val created_at: Date = Date()
)
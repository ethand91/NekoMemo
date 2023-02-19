package com.neko.nekomemo.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {
    @Query("select * from ${MemoDatabase.NAME} order by created_at desc")
    fun getAll(): MutableList<Memo>

    @Query("select * from ${MemoDatabase.NAME} where id = :id")
    fun getMemoById(id: Int?): Memo?

    @Query("delete from ${MemoDatabase.NAME}")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memo: Memo)

    @Update
    fun updateMemo(vararg memo: Memo)

    @Delete
    fun delete(memo: Memo)
}
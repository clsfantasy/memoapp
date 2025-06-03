package com.example.memoapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    fun getAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM memo WHERE id = :id LIMIT 1")
    fun getById(id: Int): Memo?

    @Query("SELECT * FROM Memo WHERE category = :category ORDER BY id DESC")
    fun getByCategory(category: String): LiveData<List<Memo>>

    @Query("SELECT * FROM Memo ORDER BY id DESC")
    fun getAllSorted(): LiveData<List<Memo>>

    @Insert
    suspend fun insert(memo: Memo)

    @Update
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Query("DELETE FROM memo WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM Memo")
    suspend fun getAllSuspend(): List<Memo>
}

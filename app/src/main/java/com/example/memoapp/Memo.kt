package com.example.memoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "默认",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)

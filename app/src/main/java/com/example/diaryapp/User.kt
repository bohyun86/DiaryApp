package com.example.diaryapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "userPw") val userPw: String,
    @ColumnInfo(name = "userEmail") val userEmail: String
)

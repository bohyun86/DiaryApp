package com.example.diaryapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "content",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.RESTRICT
    )]
)
data class Content(
    @PrimaryKey @ColumnInfo(name = "contentId") val contentId: String,
    @ColumnInfo(name = "date") val contentDate: String,
    @ColumnInfo(name = "contentDetail") val contentDetail: String,
    @ColumnInfo(name = "userId") val userId: String
)

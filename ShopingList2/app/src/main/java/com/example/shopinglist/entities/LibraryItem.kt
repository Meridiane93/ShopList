package com.example.shopinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// save hints
@Entity(tableName = "library")
data class LibraryItem(
    @PrimaryKey(autoGenerate = true) // one column
    val id: Int?,

    @ColumnInfo(name = "name") // two column
    val name: String
)

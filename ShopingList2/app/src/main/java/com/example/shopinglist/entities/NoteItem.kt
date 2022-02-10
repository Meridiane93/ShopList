package com.example.shopinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "note_list")
data class NoteItem(
    @PrimaryKey(autoGenerate = true) // one column
    val id: Int?,                     // @PrimaryKey auto generate Int to id and one column for roomLibrary

    @ColumnInfo(name = "Title") // two column
    val title: String,          // two column, note title

    @ColumnInfo(name = "content") // three column
    val content: String,          // three column, note content

    @ColumnInfo(name = "time") // four column
    val time: String,          // four column, note time

    @ColumnInfo(name = "category") // five column
    val category: String           // five column, note category
): Serializable // позволяет передавать и принимать целым классом ( запихали в интент)

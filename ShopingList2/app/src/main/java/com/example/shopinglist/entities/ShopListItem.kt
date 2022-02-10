package com.example.shopinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_list_item")
data class ShopListItem(
    @PrimaryKey(autoGenerate = true) // one column
    val id: Int?,                    // @PrimaryKey auto generate Int to id and one column for roomLibrary

    @ColumnInfo(name = "name") // two column
    val name: String,          // two column for androidProject

    @ColumnInfo(name = "itemInfo") // three column
    val itemInfo: String = "",         // three column for androidProject ,nameItem (example: potatoes 300 grams)

    @ColumnInfo(name = "itemChecked") // four column
    val itemChecked: Boolean = false,         // four column for androidProject ,purchase verification

    @ColumnInfo(name = "listId") // five column
    val listId: Int,             // five column for android ,id itemInfo

    @ColumnInfo(name = "itemType") // six column
    val itemType: Int = 0          // six column for android ,hints
)

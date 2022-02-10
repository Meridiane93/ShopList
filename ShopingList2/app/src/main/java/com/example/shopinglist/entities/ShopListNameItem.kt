package com.example.shopinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping_list_names")
data class ShopListNameItem(
    @PrimaryKey(autoGenerate = true) // one column
    val id: Int?,                    // @PrimaryKey auto generate Int to id and one column for roomLibrary

    @ColumnInfo(name = "name") // two column for roomLibrary
    val name: String,          // two column for androidProject

    @ColumnInfo(name = "time") // three column for roomLibrary
    val time: String,          // three column for androidProject

    @ColumnInfo(name = "allItemCounter") // four column for roomLibrary
    val allItemCounter: Int,             // four column for androidProject , all shopping list

    @ColumnInfo(name = "checkedItemsCounter") // five column for roomLibrary
    val checkedItemsCounter: Int,             // five column for androidProject ,all buy list

    @ColumnInfo(name = "itemsIds") // six column for roomLibrary
    val itemsIds: String           // six column for androidProject , id for each column
): Serializable // send the class in its entirety, not individually

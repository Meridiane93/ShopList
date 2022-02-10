package com.example.shopinglist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shopinglist.entities.LibraryItem
import com.example.shopinglist.entities.NoteItem
import com.example.shopinglist.entities.ShopListItem
import com.example.shopinglist.entities.ShopListNameItem

// build table bd
@Database(entities = [LibraryItem::class,NoteItem::class,
    ShopListItem::class,ShopListNameItem::class], version = 1)
abstract class MainDatabase: RoomDatabase() {
    abstract fun getDao(): Dao

    // companion object нужен чтобы использовать функии внутри него без инициализации MainDatabase
    companion object{
        @Volatile
        private var INSTANCE: MainDatabase ?= null

        fun getDateBase(context: Context): MainDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,MainDatabase::class.java,"shopping_list.db").build() // build db
                instance
            }
        }
    }
}
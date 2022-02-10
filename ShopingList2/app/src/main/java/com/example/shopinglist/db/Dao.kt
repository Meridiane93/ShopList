package com.example.shopinglist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shopinglist.entities.LibraryItem
import com.example.shopinglist.entities.NoteItem
import com.example.shopinglist.entities.ShopListItem
import com.example.shopinglist.entities.ShopListNameItem
import kotlinx.coroutines.flow.Flow


@Dao // work in bd
interface Dao {
    @Query("SELECT * FROM note_list") // reading
    fun getAllNotes(): Flow<List<NoteItem>>   // flow также сам обновляет список (из корутин взят)

    @Query("SELECT * FROM shopping_list_names") // reading
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>   // flow также сам обновляет список (из корутин взят)

    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId") // выбрать все элементы из таблицы shop_list_item у которых listId такой же как
    fun getAllShopListItems(listId:Int): Flow<List<ShopListItem>>   // flow также сам обновляет список (из корутин взят)

    @Query("SELECT * FROM library WHERE name LIKE :name") // выбрать все элементы из таблицы library у которых name такой же как name: String
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>


    @Query("DELETE FROM shop_list_item WHERE listId LIKE :listId") // выбрать все элементы из таблицы shop_list_item у которых listId такой же как
    suspend fun deleteShopItemsByListId(listId:Int)

    @Query("DELETE FROM note_list WHERE id IS :id") // delete
    suspend fun deleteNote(id:Int) // указываем suspend так как функию нужно запускать на второстепенном потоке, иначе будет ошибка

    @Query("DELETE FROM shopping_list_names WHERE id IS :id") // delete
    suspend fun deleteShopListName(id:Int) // указываем suspend так как функию нужно запускать на второстепенном потоке, иначе будет ошибка

    @Query("DELETE FROM library WHERE id IS :id") // delete
    suspend fun deleteLibraryItem(id:Int) // указываем suspend так как функию нужно запускать на второстепенном потоке, иначе будет ошибка

    @Insert // write in NoteItem
    suspend fun insertNote(note:NoteItem)

    @Insert // write in ShopListItem
    suspend fun insertItem(shopListItem:ShopListItem)

    @Insert // write in LibraryItem
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Insert // write in ShopListName
    suspend fun insertShopListName(nameItem:ShopListNameItem)

    @Update // update NoteItem
    suspend fun updateNote(note:NoteItem)

    @Update // update LibraryItem
    suspend fun updateLibraryItem(item:LibraryItem)

    @Update // update ShopListItem
    suspend fun updateListItem(item:ShopListItem)

    @Update // update ShoppingListName
    suspend fun updateListName(shopListNameItem:ShopListNameItem)
}
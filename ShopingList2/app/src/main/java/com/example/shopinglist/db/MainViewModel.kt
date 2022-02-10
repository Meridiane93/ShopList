package com.example.shopinglist.db

import androidx.lifecycle.*
import com.example.shopinglist.entities.LibraryItem
import com.example.shopinglist.entities.NoteItem
import com.example.shopinglist.entities.ShopListItem
import com.example.shopinglist.entities.ShopListNameItem
import kotlinx.coroutines.launch

class MainViewModel(database: MainDatabase) : ViewModel() {
    val dao = database.getDao()

    val allNotes : LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData() // считываем с бд с помощью LiveData, список заметок

    val allShopListNamesItem : LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData() // считываем с бд с помощью LiveData, список покупок

    val libraryItems = MutableLiveData<List<LibraryItem>>() // инициализировали LiveData она щас пуста, через него будем обновлять список подсказок

    fun getAllItemsFromList(listId: Int) : LiveData<List<ShopListItem>>{ // чтение из БД в зависимости от Id (номера списка) у нас будет 2 списка (подсказки,список покупок)
        return dao.getAllShopListItems(listId).asLiveData()
    }
    fun getAllLibraryItems(name: String) = viewModelScope.launch{ // чтение из БД названий подсказок, c помощью второстепенных потоков( viewModelScope.launch )
        libraryItems.postValue(dao.getAllLibraryItems(name)) // с помощью libraryItems.postValue передаём список в ShopListActivity, libraryItemObserver(),  mainViewModel.libraryItems.observe(this,{
    }
    fun insertNote(note:NoteItem) = viewModelScope.launch { // запись в БД через корутину с помощью вспомог фун.ВМ viewModelScope
        dao.insertNote(note)
    }
    fun insertShopListName(nameItem:ShopListNameItem) = viewModelScope.launch { // запись в БД через корутину с помощью вспомог фун.ВМ viewModelScope
        dao.insertShopListName(nameItem)
    }
    fun insertShopItem(shopListItem:ShopListItem) = viewModelScope.launch { // запись в БД через корутину с помощью вспомог фун.ВМ viewModelScope
        dao.insertItem(shopListItem)
        if (!isLibraryItemExists(shopListItem.name)) dao.insertLibraryItem(LibraryItem(null,shopListItem.name)) // если слова нет в БД подсказок то записываем его
    }
    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch { // обновить данные в БД
        dao.updateLibraryItem(item)
    }
    fun updateNote(note:NoteItem) = viewModelScope.launch { // обновить данные в БД
        dao.updateNote(note)
    }
    fun updateListName(shopListNameItem:ShopListNameItem) = viewModelScope.launch { // обновить данные в БД
        dao.updateListName(shopListNameItem)
    }
    fun updateListItem(shopListItem:ShopListItem) = viewModelScope.launch { // обновить данные в БД о интерфейсе
        dao.updateListItem(shopListItem)
    }
    fun deleteNote(id:Int) = viewModelScope.launch { // удалить из БД deleteNote по идентификатору
        dao.deleteNote(id)
    }
    fun deleteLibraryItem(id:Int) = viewModelScope.launch { // удалить из БД Library по идентификатору
        dao.deleteLibraryItem(id)
    }
    fun deleteShopList(id:Int, deleteList: Boolean) = viewModelScope.launch { // удалить из ShopListName БД по идентификатору
        if (deleteList)dao.deleteShopListName(id)
        dao.deleteShopItemsByListId(id)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean { // проверяем есть ли такое слово в базе данных с подсказками
        return dao.getAllLibraryItems(name).isNotEmpty()
    }

    // так рекомендуют инициализировать VM в Google каждый раз, а не напрямую
    class MainViewModelFactory(val database: MainDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {    // создаёт VM
            if (modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}
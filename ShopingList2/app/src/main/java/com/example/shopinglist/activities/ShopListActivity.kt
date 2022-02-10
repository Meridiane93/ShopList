package com.example.shopinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopinglist.R
import com.example.shopinglist.databinding.ActivityShopListBinding
import com.example.shopinglist.db.MainViewModel
import com.example.shopinglist.db.ShopListItemAdapter
import com.example.shopinglist.dialogs.EditListItemDialog
import com.example.shopinglist.entities.LibraryItem
import com.example.shopinglist.entities.ShopListItem
import com.example.shopinglist.entities.ShopListNameItem
import com.example.shopinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(),ShopListItemAdapter.Listener {

    private lateinit var binding: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null // информация о том на какой элемент мы нажали
    private lateinit var saveItem: MenuItem // кнопка сохранить
    private var edItem: EditText? = null // передаём значения из editText в hint в shop_list_menu
    private lateinit var textWatcher: TextWatcher // отображение подсказок (hint)
    private var adapter: ShopListItemAdapter ?= null
    private lateinit var defPref: SharedPreferences
    private var currentTheme = "" // сохраняется Theme которая сейчас

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем SharedPreferences
        currentTheme = defPref.getString("theme_key", "green").toString() // инициализируем currentTheme
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // подключаем меню
        menuInflater.inflate(R.menu.shop_list_menu, menu) // надуваем меню которое хотим подключить
        saveItem = menu?.findItem(R.id.save_item)!!  // находим кнопку сохранить
        val newItem = menu.findItem(R.id.new_item)   // находим кнопку добавить
        edItem =
            newItem.actionView.findViewById(R.id.adNewShopItem) as EditText// находим и присваиваем эдит текст
        newItem.setOnActionExpandListener(expandActionView()) // добавляем слушатель для кнопки new_item
        saveItem.isVisible = false
        textWatcher = textWatcher() // добавляем textWatcher() в переменную
        return true
    }

    private fun textWatcher(): TextWatcher { // инициализация TextWatcher
        return object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { // запускается при написании любой буквы в эдит текст
                mainViewModel.getAllLibraryItems("%$p0%")
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // слушатель на нажатия кнопок из меню
        when (item.itemId) {
            R.id.save_item -> addNewShopItem(edItem?.text.toString()) // при нажатии на кнопку сохранить
            R.id.delete_list -> { // при нажатии на кнопку удалить
                 mainViewModel.deleteShopList(shopListNameItem?.id!! ,true)
                 finish()
            }
            R.id.clear_list -> mainViewModel.deleteShopList(shopListNameItem?.id!! ,false)
            R.id.share_list -> startActivity(Intent.createChooser
                (ShareHelper.shareShopList(adapter?.currentList!!,shopListNameItem?.name!!),"Поделиться с помошью"))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String) { // добавление данных в ShopListItem и отправляем в VM
        if (name.isEmpty()) return

        val item = ShopListItem(
            null, name, "", false, shopListNameItem?.id!!, 0
        )
        edItem?.setText("") // очищаем написанное после нажатия кнопки сохранить
        mainViewModel.insertShopItem(item)
    }

    private fun listItemObserver() { // обновление списка с данными у LiveData
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this, {
            adapter?.submitList(it) // submitList делаем только когда список не равен нулю

            // отображение текста "Нет элементов"
            binding.tvEmpty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this,{  // происходят изменения в LiveData
            val tempShopList = ArrayList<ShopListItem>() // создаём пустой список из ShopListItem
            it.forEach { item ->// перебираем список подсказок , item это LibraryItem
                val shopItem = ShopListItem( // заполняем ShopListItem
                    item.id, item.name, "", false, 0, 1) // itemType при указании 1, показывается разметка для подсказок
                tempShopList.add(shopItem) // передаём заполненный ShopListItem
            }
            adapter?.submitList(tempShopList) // обновляем список в адаптере
            binding.tvEmpty.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun initRcView() = with(binding){ // инициализируем RcView
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter = adapter
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener { // следит за edit_action_layout
        return object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean { // когда открывается ActionView (edit_action_layout)
                saveItem.isVisible = true
                edItem?.addTextChangedListener (textWatcher) // добавляем textWatcher
                libraryItemObserver() // запускаем новый обсервер который обновляет список подсказок
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity) // останавливаем обсервер c формированием списка
                mainViewModel.getAllLibraryItems("%%") // покажет весь список подсказок
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean { // когда закрывается ActionView (edit_action_layout)
                saveItem.isVisible = false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu() // перерисовывает мену
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity) // останавливаем обсервер после закрытия эдит
                edItem?.setText("")
                listItemObserver() // запускаем обсервер с списком
                return true
            }
        }
    }

    private fun init() { // инициализируем RcView и получаем из интента shoppingList чтобы узнать какой список открыли
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
        binding.tvEmpty.setText(R.string.text_empty)
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) { // выбираем что запустить (если нажимаем кнопку редактирования то editListItem(shopListItem), если checkBox то  mainViewModel.updateListItem(shopListItem))
        when(state){
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem)  // обновить данные в shopListItem
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)  // запускаем диалог
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem)  // запускаем диалог подсказок
            ShopListItemAdapter.DELETE_LIBRARY_ITEM ->{  // удаляем подсказку из диалога
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") // обновляем список подсказок сразу после редактирования
            }
            ShopListItemAdapter.ADD -> {  // Добавляем из подсказок слово в список покупок
                addNewShopItem(shopListItem.name)
            }
        }
    }

    private fun editListItem(item:ShopListItem){ // запускаем диалог
        EditListItemDialog.showDialog(this,item, object :EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) { // запускаем интерфейс у EditListItemDialog
                mainViewModel.updateListItem(item)
            }
        })
    }
    private fun editLibraryItem(item:ShopListItem){ // переиспользуем диалог редактирования списка
        EditListItemDialog.showDialog(this,item, object :EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) { // запускаем интерфейс у EditListItemDialog
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name)) // делаем перегрузку, заполняем LibraryItem данными из ShopListItem
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") // обновляем список подсказок сразу после редактирования
            }
        })
    }

    private fun saveItemCount(){ // считаем количество купленных покупок(по галочкам в чек-бокс)
        var checkedCounter = 0 // количество с галочкой чек-бокс
        adapter?.currentList?.forEach { // считаем количество с галочкой чек-бокс
            if (it.itemChecked) checkedCounter++
        }
        val tempShopListNameItem = shopListNameItem?.copy( // перезаписываем данные в shopListNameItem с помощью copy
            allItemCounter = adapter?.itemCount!!, // перезаписываем сколько находится элементов в списке (чего нужно купить)
            checkedItemsCounter = checkedCounter  //  перезаписываем сколько с галочкой чек-бокс
        )
        mainViewModel.updateListName(tempShopListNameItem!!) // обновляем список в БД
    }

    override fun onBackPressed() { // отслеживаем нажатие кнопки назад
        saveItemCount()
        super.onBackPressed()
    }

    companion object {
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    private fun getSelectedTheme():Int{ // определяем какая тема была выбрана
        return if (defPref.getString("theme_key", "green") == "green") R.style.Theme_ShopingListBlue
        else R.style.Theme_ShopingListRed
    }
}
package com.example.shopinglist.db

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopinglist.R
import com.example.shopinglist.databinding.ShopLibraryListItemBinding
import com.example.shopinglist.databinding.ShopListItemBinding
import com.example.shopinglist.entities.ShopListItem

class ShopListItemAdapter(private val listener:Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    // если из getItemViewType пришло 1, то используем разметку fun createLibrary, 0 то  fun createShopItem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { // создаёт Holder (разметку для заполнения)
        return if (viewType == 0) ItemHolder.createShopItem(parent) // создаётся разметка createShopItem
               else ItemHolder.createLibrary(parent) // создаётся разметка createLibrary
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {  // заполняет данными разметку
        if (getItem(position).itemType == 0) holder.setItemData(getItem(position),listener)
        else holder.setLibraryData(getItem(position),listener)
    }

    // ViewType нужен для того чтобы в RcView показывать разные разметки
    override fun getItemViewType(position: Int): Int { // возвращает в CreateViewHolder ViewType с позицией (0 или 1) элемент для библиотеки (0), элементы для списка (1)
        return getItem(position).itemType
    }

    class ItemHolder(val view:View) : RecyclerView.ViewHolder(view){

        fun setItemData(shopListItem:ShopListItem, listener:Listener){  // заполняет 1 разметку данными (список)
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                checkBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)

                checkBox.setOnClickListener { // слушатель checkBox
                    listener.onClickItem(shopListItem.copy(itemChecked = checkBox.isChecked),
                        CHECK_BOX) // передаём состояние checkBox в shopListItem
                }
                imEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
            }
        }
        fun setLibraryData(shopListItem:ShopListItem, listener:Listener){  // заполняет 2 разметку данными (подсказки)
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name

                imEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                imDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener { // itemView слушатель при нажатии на весь элемент
                    listener.onClickItem(shopListItem, ADD)
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding){ //  изменение текста при выборе в чек-боксе
            binding.apply {
                if (checkBox.isChecked){
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // вычеркнуть текст у tvName
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // вычеркнуть текст у tvInfo
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                }else{
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG // вернуть текст у tvName
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG // вернуть текст у tvInfo
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }
        }

        private fun infoVisibility(shopListItem:ShopListItem): Int { // проверяем tvInfo.text если там данные, если есть то показываем
            return if (shopListItem.itemInfo.isNullOrEmpty()) View.GONE
            else View.VISIBLE
        }

        companion object{  // разметка которая загружена уже в память
            fun createShopItem(parent: ViewGroup) : ItemHolder =
                ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.shop_list_item,parent,false))

            fun createLibrary(parent: ViewGroup) : ItemHolder =
                ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.shop_library_list_item,parent,false))
        }
    }
    // индексация списка и сравнение элементов
    class ItemComparator: DiffUtil.ItemCallback<ShopListItem>(){
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean{
            return oldItem.id == newItem.id // сравнивает элементы на похожесть
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
           return oldItem == newItem // сравнивает весь контент на похожесть
        }
    }

    // кнопка удалить в note_list_item
    interface Listener{
        fun onClickItem(shopListItem:ShopListItem, state: Int) // выбранный элемент shop_list_item
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }
}
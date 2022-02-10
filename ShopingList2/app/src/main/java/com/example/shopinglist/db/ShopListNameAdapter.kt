package com.example.shopinglist.db

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopinglist.R
import com.example.shopinglist.databinding.ListNameItemBinding
import com.example.shopinglist.entities.ShopListNameItem

class ShopListNameAdapter(private val listener:Listener) : ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder.create(parent) // создаёт Holder (разметку для заполнения)

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.setData(getItem(position),listener)  // заполняет данными разметку


    class ItemHolder(view:View) : RecyclerView.ViewHolder(view){

        private val binding = ListNameItemBinding.bind(view)

        fun setData(shopListNameItem:ShopListNameItem, listener:Listener) = with(binding){  // заполняет разметку данными
            tvListName.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time
            val counterText = "${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCounter}"
            tvCounter.text = counterText
            pBar.max = shopListNameItem.allItemCounter // прогресс бар максимальное количество
            pBar.progress = shopListNameItem.checkedItemsCounter // прогресс бар на сколько продвинулись
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem,binding.root.context)) // colorState специальный класс
            counterCard.backgroundTintList = colorState // меняем цвет карточки
            pBar.progressTintList = colorState // задаём прогресс бару цвет полосы
            itemView.setOnClickListener {  // слушатель нажатий на весь элемент
                listener.onClickItem(shopListNameItem)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!) // передаём id в fun deleteItem(id:Int)
            }
            imEdit.setOnClickListener {
                listener.editItem(shopListNameItem) // передаём shopListNameItem в fun editItem(shopListName:ShoppingListName)
            }
        }
        companion object{  // разметка которая загружена уже в память
            fun create(parent: ViewGroup) : ItemHolder =
                ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_name_item,parent,false))
        }
        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int { // изменение цвета в прогресс баре
            return if (item.checkedItemsCounter == item.allItemCounter) ContextCompat.getColor(context, R.color.picker_green)
            else ContextCompat.getColor(context, R.color.picker_reds)
        }
    }

    // индексация списка и сравнение элементов
    class ItemComparator: DiffUtil.ItemCallback<ShopListNameItem>(){
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean{
            return oldItem.id == newItem.id
        } // сравнивает элементы на похожесть

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
           return oldItem == newItem // сравнивает весь контент на похожесть
        }
    }

    // кнопка удалить в note_list_item
    interface Listener{
        fun deleteItem(id:Int) // удалить по идентификатору
        fun editItem(shopListNameItem:ShopListNameItem)  // обновить по ShoppingListName
        fun onClickItem(shopListNameItem:ShopListNameItem) // выбранный элемент в fragment_shop_list_names
    }
}
package com.example.shopinglist.db

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopinglist.R
import com.example.shopinglist.databinding.NoteListItemBinding
import com.example.shopinglist.entities.NoteItem
import com.example.shopinglist.utils.HtmlManager
import com.example.shopinglist.utils.TimeManager

class NoteAdapter(private val listener:Listener, private val defPref:SharedPreferences) : ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder = ItemHolder.create(parent) // создаёт Holder (разметку для заполнения)

    override fun onBindViewHolder(holder: ItemHolder, position: Int) = holder.setData(getItem(position), listener, defPref)  // заполняет данными разметку


    class ItemHolder(view:View) : RecyclerView.ViewHolder(view){

        private val binding = NoteListItemBinding.bind(view)

        fun setData(note:NoteItem, listener: Listener, defPref:SharedPreferences) = with(binding){  // заполняет разметку данными
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time, defPref)
            itemView.setOnClickListener {  // слушатель нажатий на весь элемент
                listener.onClickItem(note)
            }
            imDelet.setOnClickListener {
                listener.deleteItem(note.id!!)
            }
        }
        companion object{  // разметка которая загружена уже в память
            fun create(parent: ViewGroup) : ItemHolder =
                ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_list_item,parent,false))
        }
    }
    // индексация списка и сравнение элементов
    class ItemComparator: DiffUtil.ItemCallback<NoteItem>(){
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean{
            return oldItem.id == newItem.id
        } // сравнивает элементы на похожесть

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
           return oldItem == newItem // сравнивает весь контент на похожесть
        }
    }

    // кнопка удалить в note_list_item
    interface Listener{
        fun deleteItem(id:Int) // удалить по идентификатору
        fun onClickItem(note:NoteItem) // выбранный элемент note_list_item
    }
}
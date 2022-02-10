package com.example.shopinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.shopinglist.databinding.EditListItemDialogBinding
import com.example.shopinglist.entities.ShopListItem

// объект диалога
object EditListItemDialog { // запускаем диалог edit_list_item_dialog
    fun showDialog(context: Context, item:ShopListItem, listener:Listener){
        var dialog: AlertDialog ?= null
        val builder = AlertDialog.Builder(context) // инициализируем диалог
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context)) // указываем Builder какую разметку использовать
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name) // перед запуском диалога заполняем текст edName
            edInfo.setText(item.itemInfo) // перед запуском диалога заполняем текст edInfo
            if (item.itemType == 1) edInfo.visibility = View.GONE // прячем второй текст при переиспользовании диалога ( нажатии кнопки редактировть в подсказках )
            bUpdate.setOnClickListener { // слушатель кнопки
                if(edName.text.toString().isNotEmpty()){
                    listener.onClick(item.copy(name = edName.text.toString(), itemInfo = edInfo.text.toString())) // обновляем данные в ShopListItem
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null) // убираем стандартный фон диалога
        dialog.show() // показываем диалог
    }
    interface Listener { // передаём данные при нажатии в ShopListItem
        fun onClick(item:ShopListItem)
    }
}
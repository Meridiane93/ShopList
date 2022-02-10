package com.example.shopinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.shopinglist.R
import com.example.shopinglist.databinding.NewListDialogBinding

// объект диалога
object NewListDialog {
    fun showDialog(context: Context, listener:Listener, name: String){
        var dialog: AlertDialog ?= null
        val builder = AlertDialog.Builder(context) // инициализируем диалог
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root) // указываем Builder какую разметку использовать
        binding.apply {

            edNewListName.setText(name)
            if (name.isNotEmpty()) bCreate.text = context.getString(R.string.update_text_dialogs)

            bCreate.setOnClickListener {
                val listName = edNewListName.text.toString()
                if (listName.isNotEmpty()){
                    listener.onClick(listName) // передаём текст из edNewListName в onClick(name:String)
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null) // убираем стандартный фон диалога
        dialog.show() // показываем диалог
    }
    interface Listener {// передаём данные при нажатии
        fun onClick(name:String)
    }
}
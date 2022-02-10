package com.example.shopinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.shopinglist.databinding.DeleteDialogBinding
import com.example.shopinglist.databinding.NewListDialogBinding

// объект диалога
object DeletDialog {
    fun showDialog(context: Context, listener:Listener){
        var dialog: AlertDialog ?= null
        val builder = AlertDialog.Builder(context) // инициализируем диалог
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root) // указываем Builder какую разметку использовать
        binding.apply {
            bDelet.setOnClickListener { // нажали кнопку удалить
                listener.onClick()
                dialog?.dismiss()
            }
            bCancel.setOnClickListener { // нажали кнопку отменить
                dialog?.dismiss() // закрывает диалог
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null) // убираем стандартный фон диалога
        dialog.show() // показываем диалог
    }
    interface Listener {// передаём данные при нажатии
        fun onClick()
    }
}
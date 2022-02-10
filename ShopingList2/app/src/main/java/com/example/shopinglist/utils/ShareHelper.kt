package com.example.shopinglist.utils

import android.content.Intent
import com.example.shopinglist.entities.ShopListItem

object ShareHelper { // отправка текста в другие соц сети
    fun shareShopList(shopList : List<ShopListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList,listName))
        }
        return intent
    }

    private fun makeShareText(shopList : List<ShopListItem>, listName: String): String{ // формируем текст для отправки
        val sBuilder = StringBuilder() // собирает текст
        sBuilder.append("<<$listName>>") // заголовок
        sBuilder.append("<<\n>>") // переход на след строку
        var counter = 0
        shopList.forEach{
            sBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")
            sBuilder.append("<<\n>>")
        }
        sBuilder.append("Конец списка")

        return sBuilder.toString()
    }
}
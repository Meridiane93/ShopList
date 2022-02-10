package com.example.shopinglist.utils

import android.text.Html
import android.text.Spanned

//
object HtmlManager {
    // указываем стили и прочее
    fun getFromHtml(text:String): Spanned {  // берёт тектс из БД и выдаст где текст оформлен в стиле(жирный, выделен и тд.)
        // проверка версии, на старой версии по другому в ветке if
        return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) Html.fromHtml(text)
        else  Html.fromHtml(text,Html.FROM_HTML_MODE_COMPACT)
    }

    // Html который будем сохранять
    fun toHtml(text:Spanned): String{
        return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) Html.toHtml(text)
        else  Html.toHtml(text,Html.FROM_HTML_MODE_COMPACT)
    }
}
package com.example.shopinglist.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    const val DEF_TIME_FORMAT = "hh:mm:ss - dd/MM/yy"
    fun getCurrentTime(): String { // получаем текущее время
        val formatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault()) // какого формата хотим получить дату и время
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, defPreferences: SharedPreferences): String { // получаем формат из настроек и возвращаем в каком он формате
        val defFormatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault())
        val defDate = defFormatter.parse(time) // парсим string полученный из Pref
        val newFormat = defPreferences.getString("time_format_key", DEF_TIME_FORMAT) // берём из SharedPreferences строку ( выбранный формат времени) по ключу, и значение по умолчанию
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())

        return if (defDate != null) newFormatter.format(defDate) // возвращаем новый формат времени
               else time
    }
}
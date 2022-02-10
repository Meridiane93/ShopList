package com.example.shopinglist.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.example.shopinglist.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defPref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем SharedPreferences
        setTheme(getSelectedTheme())
        setContentView(R.layout.activity_settings)
        if(savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.placeHolder,SettingsFragment()).commit() // запускаем фрамгент  с помощью  FragmentManager/ replace заменяет R.id.placeHolder на фрагмент SettingsFragment ( всегда помещает только фрагменты )

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // активируем кнопку назад
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // назначаем нажатие на кнопку назад
        if (item.itemId == android.R.id.home) finish() // если нажали на кнопку назад выходим из фрагмента
        return super.onOptionsItemSelected(item)
    }
    private fun getSelectedTheme():Int{ // определяем какая тема была выбрана
        return if (defPref.getString("theme_key", "green") == "green") R.style.Theme_ShopingListBlue
        else R.style.Theme_ShopingListRed
    }
}
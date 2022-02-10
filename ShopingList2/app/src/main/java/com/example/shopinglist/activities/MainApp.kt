package com.example.shopinglist.activities

import android.app.Application
import com.example.shopinglist.db.MainDatabase

// Application() базовый класс активити (в нём находятся все активити приложения) в нём инициализируем бд,тогда из любой активити можем получить доступ к бд
class MainApp: Application() {
    val database by lazy { MainDatabase.getDateBase(this) }

}
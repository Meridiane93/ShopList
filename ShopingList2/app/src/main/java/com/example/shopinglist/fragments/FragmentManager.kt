package com.example.shopinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import com.example.shopinglist.R

object FragmentManager {
    var currentFrag: BaseFragment ?= null // какой фрагмент сейчас запущен в активити

    // в свойствах, newFrag: фрагмент который передаём и activity контекст активити
    fun setFragment(newFrag:BaseFragment, activity: AppCompatActivity){
        val transaction = activity.supportFragmentManager.beginTransaction() // позволяет переключаться между фрагментами
        transaction.replace(R.id.placeHolder, newFrag) // помещаем(заменяем) фрагмент в активити
        transaction.commit() // применить изменения
        currentFrag = newFrag // записывается текущий открытый фрагмент
    }
}
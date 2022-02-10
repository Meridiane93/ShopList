package com.example.shopinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.shopinglist.R
import com.example.shopinglist.billing.BillingManager
import com.example.shopinglist.databinding.ActivityMainBinding
import com.example.shopinglist.dialogs.NewListDialog
import com.example.shopinglist.fragments.FragmentManager
import com.example.shopinglist.fragments.NoteFragment
import com.example.shopinglist.fragments.ShopListNamesFragment
import com.example.shopinglist.settings.SettingsActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity(),NewListDialog.Listener {

    lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currentMenuItemId = R.id.shop_list // сохраняется вкладка которая была открыта
    private var currentTheme = "" // сохраняется Theme которая сейчас
    private var iAd: InterstitialAd ?= null
    private var adShowCounter = 0
    private var adShowCounterMax = 2
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this) // инициализируем SharedPreferences
        currentTheme = defPref.getString("theme_key", "green").toString() // инициализируем currentTheme
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
        setBottomNavListener()
        if(pref.getBoolean(BillingManager.REMOVE_ADS_KEY,false)) loadInterAd()
    }

    private fun loadInterAd(){ // каждый раз подгужать новую рекламу
        val request = AdRequest.Builder().build() // запрос для получения рекламы
        InterstitialAd.load(this,getString(R.string.inter_ad_id),request,object : InterstitialAdLoadCallback(){ // загружаем рекламу
            override fun onAdLoaded(ad: InterstitialAd) { // в ad записывается объявление которое успешно загрузилось
                iAd = ad
            }

            override fun onAdFailedToLoad(p0: LoadAdError) { // если объявление не загрузилось
                iAd = null
            }
        })
    }

    private fun showInterAd(adListener:AdListener){ // показываем объявление
        if (iAd != null ){
            iAd?.fullScreenContentCallback = object: FullScreenContentCallback(){    // fullScreenContentCallback смотрит за объявлением, что сделал пользователь
                override fun onAdDismissedFullScreenContent() { // как только пользователь закрыл объявление
                    iAd = null
                    loadInterAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) { // если произошла ошибка
                    iAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() { // если объявление полностью было показано
                    iAd = null
                    adShowCounter = 0
                    loadInterAd()
                    adListener.onFinish()
                }
            }
            iAd?.show(this) // показываем объявление
        } else {
            adShowCounter++
            adListener.onFinish()
        }
    }

    private fun setBottomNavListener(){
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings ->{
                    showInterAd(object : AdListener{
                        override fun onFinish() {
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                        }
                    })
                }
                R.id.notes -> {
                    showInterAd(object : AdListener{
                        override fun onFinish() {
                            currentMenuItemId = R.id.notes // отправляем в currentMenuItemId какая вкладка открыта
                            FragmentManager.setFragment(NoteFragment.newInstance(),this@MainActivity)
                        }
                    })
                }

                R.id.shop_list ->{
                    currentMenuItemId = R.id.shop_list // отправляем в currentMenuItemId какая вкладка открыта
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
                }
                R.id.new_item -> FragmentManager.currentFrag?.onClickNew()
            }
            true
        }
    }

    private fun getSelectedTheme():Int{ // определяем какая тема была выбрана
        return if (defPref.getString("theme_key", "green") == "green") R.style.Theme_ShopingListBlue
               else R.style.Theme_ShopingListRed
    }

    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "green") != currentTheme) recreate() // пересоздаём активити
    }

    override fun onClick(name: String) {
    }

    interface AdListener{
        fun onFinish()
    }
}
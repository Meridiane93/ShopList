package com.example.shopinglist.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.shopinglist.R
import com.example.shopinglist.billing.BillingManager

class SettingsFragment: PreferenceFragmentCompat() {

    private lateinit var removeAdsPref: Preference
    private lateinit var bManager: BillingManager


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // показываем разметку фрагментов с помощью Preferences
        setPreferencesFromResource(R.xml.settings_preference, rootKey) // из какого ресурса импортировать фрагмент
        init()
    }
    private fun init(){
        bManager = BillingManager(activity as AppCompatActivity)
        removeAdsPref = findPreference("remove_ads_key")!!
        removeAdsPref.setOnPreferenceClickListener {
            bManager.startConnection()
            true
        }
    }

    override fun onDestroy() {
        bManager.closeConnection()
        super.onDestroy()
    }
}
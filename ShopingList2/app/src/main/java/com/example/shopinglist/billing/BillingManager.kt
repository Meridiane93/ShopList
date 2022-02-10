package com.example.shopinglist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

class BillingManager(val activity:AppCompatActivity) { // встроенные покупки
    private var bClient: BillingClient ?= null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient(){
        bClient = BillingClient // настроили BillingClient с помощью которого осуществляется подключение к плей маркет
            .newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases()
            .build()
    }
    private fun savePref(isPurchased:Boolean){
        val pref =  activity.getSharedPreferences(MAIN_PREF,Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_ADS_KEY,isPurchased)
        editor.apply()
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener{ // слушатель который ожидает когда реализуется покупка (пока пользователь не нажмёт купить) Когда покупка запустится успешно то срабатывает функия эта
        return PurchasesUpdatedListener { bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }
            }
        }
    }

    fun startConnection(){
        bClient?.startConnection( object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() { // если BillingService не подключается
            }

            override fun onBillingSetupFinished(p0: BillingResult) { // если BillingService  подключился
                getItem()
            }
        })
    }

    private fun getItem(){ // чтобы связаться с плей сервисом выдал продукт показал цену
        val skuList = ArrayList<String>()
        skuList.add(REMOVE_AD_ITEM) // название нашего продукта
        val skuDetails = SkuDetailsParams.newBuilder() // детали нашей покупки ( что там за покупка )
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP) // указываем что за это покупка INAPP , не подписка как можно

        bClient?.querySkuDetailsAsync(skuDetails.build()) { bReult,list ->     // вызывается диалог покупки асинхронно
            kotlin.run {
                if (bReult.responseCode == BillingClient.BillingResponseCode.OK){
                    if (list != null){
                        if (list.isNotEmpty()){
                           val bFlowParams = BillingFlowParams.newBuilder().setSkuDetails(list[0]).build()
                            bClient?.launchBillingFlow(activity,bFlowParams) // запускаем диалог покупки
                        }
                    }
                }
            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase){ // здесь подтверждаем покупку
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){ // проверка покупки
            if (!purchase.isAcknowledged){
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build() // подтверждение покупки
                bClient?.acknowledgePurchase(acParams){
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePref(true)
                        Toast.makeText(activity, "Спасибо за покупку", Toast.LENGTH_LONG).show()
                    } else{
                        Toast.makeText(activity,"Не удалось проввести покупку", Toast.LENGTH_LONG).show()
                        savePref(false)
                    }
                }
            }

        }
    }

    fun closeConnection(){ //
        bClient?.endConnection()
    }

    companion object{
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}
package com.lafimsize.inappbilling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.lafimsize.inappbilling.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val pUL= PurchasesUpdatedListener { billingResult, purchase ->

            if (billingResult.responseCode==BillingResponseCode.OK){
                println("satın alındı!!")
                println(purchase)
            }else{
                println("başarısız satın alma!!")
            }

        }

        val billingClien=BillingClient.newBuilder(this)
            .setListener(pUL)
            .enablePendingPurchases()
            .build()


        billingClien.startConnection(object :BillingClientStateListener{

            override fun onBillingServiceDisconnected() {
                println("billing service disconnected..")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                println("billing setup finished..")


                if (billingResult.responseCode==BillingClient.BillingResponseCode.OK){

                    println("billing result is ok..")

                    val products
                    = listOf(QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .setProductId("elmas_30")
                        .build(),QueryProductDetailsParams.Product.newBuilder()
                        .setProductType(BillingClient.ProductType.INAPP)
                        .setProductId("elmas_50")
                        .build()
                    )


                    val params=QueryProductDetailsParams.newBuilder()
                        .setProductList(products)

                    billingClien.queryProductDetailsAsync(params.build())
                    { billingResult2, productDetailsList ->

                        for( i in productDetailsList){
                            println(i)


                            val productDetailsParamsList= listOf(

                                BillingFlowParams
                                    .ProductDetailsParams
                                    .newBuilder()
                                    .setProductDetails(i)
                                    .build()
                            )

                            //
                            val billingFlowParams=BillingFlowParams
                                .newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()

                            //birden fazla ürün varsa hepsini başlatır for içindesin
                            billingClien.launchBillingFlow(this@MainActivity,billingFlowParams)

                        }
                    }
                }

            }
        })




    }


}
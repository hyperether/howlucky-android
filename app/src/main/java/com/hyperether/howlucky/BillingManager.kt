package com.hyperether.howlucky

import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    context: Context,
    private val activity: HowLuckyActivity
) {

    private lateinit var productDetails: ProductDetails
    private lateinit var purchase: Purchase
    private val productId = "premium1"
    private val mProductDetailsList = ArrayList<ProductDetails>()

    private val _productName = MutableStateFlow("Searching...")
    val productName = _productName.asStateFlow()

    private val _buyEnabled = MutableStateFlow(false)
    val buyEnabled = _buyEnabled.asStateFlow()

    private val _consumeEnabled = MutableStateFlow(false)
    val consumeEnabled = _consumeEnabled.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.d("BILLTEST", "Product: " + billingResult.responseCode)

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.USER_CANCELED
            ) {
                Log.d("BILLTEST", "Purchase Canceled")
            } else {
                Log.d("BILLTEST", "Purchase Error")
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun queryProduct(productId: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            BillingClient.ProductType.SUBS
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                mProductDetailsList.clear()
                mProductDetailsList.addAll(productDetailsList)

                val productDetails = productDetailsList[0]
                Log.d("BILLTEST", "Product: " + productDetails.name)

                val subOfferDetails = productDetails.subscriptionOfferDetails
                val selectedOfferToken = subOfferDetails!![0].offerToken
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                        .setProductDetails(productDetails)
                        // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                        // for a list of offers that are available to the user
                        .setOfferToken(selectedOfferToken)
                        .build()
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                // Launch the billing flow
                val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                Log.d("BILLTEST", "billingResult " + billingResult.responseCode)
            } else {
                Log.d("BILLTEST", "No Matching Products Found ")
            }
        }
    }


    fun open() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    queryProduct(productId)
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun handlePurchase(item: Purchase) {
        purchase = item
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // TODO: check this logic
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                coroutineScope.launch {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            } else {
                _buyEnabled.value = false
                _consumeEnabled.value = true
                Log.d("BILLTEST", "Purchase Completed")
                consumePurchase()
            }
        }
    }

    fun consumePurchase() {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        coroutineScope.launch {
            val result = billingClient.consumePurchase(consumeParams)

            if (result.billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
            ) {
                Log.d("BILLTEST", "Purchase Consumed")
                _buyEnabled.value = true
                _consumeEnabled.value = false
            } else {
                Log.e("BILLTEST", "Purchase Consume fail")
            }
        }
    }
}
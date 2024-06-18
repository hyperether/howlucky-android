package com.hyperether.howlucky

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.ProductDetails
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.dto.QUser
import com.qonversion.android.sdk.dto.QonversionError
import com.qonversion.android.sdk.dto.QonversionErrorCode
import com.qonversion.android.sdk.dto.entitlements.QEntitlement
import com.qonversion.android.sdk.dto.entitlements.QEntitlementRenewState
import com.qonversion.android.sdk.dto.offerings.QOfferings
import com.qonversion.android.sdk.dto.products.QProduct
import com.qonversion.android.sdk.dto.properties.QUserProperties
import com.qonversion.android.sdk.listeners.QonversionEntitlementsCallback
import com.qonversion.android.sdk.listeners.QonversionOfferingsCallback
import com.qonversion.android.sdk.listeners.QonversionUserCallback
import com.qonversion.android.sdk.listeners.QonversionUserPropertiesCallback
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern

class QonversionViewModel() : ViewModel() {

    val ENTITLEMENT_ID = "business_card_service_access"
    private lateinit var mproduct: QProduct
    private val productDetails: MutableMap<String, ProductDetails> = HashMap()
    lateinit var products: List<QProduct>

    fun loadOffer(activity: Activity) {
        Qonversion.shared.offerings(object : QonversionOfferingsCallback {
            override fun onSuccess(offerings: QOfferings) {
                val mainOffering = offerings.main
                if (mainOffering != null && mainOffering.products.isNotEmpty()) {
                    products = mainOffering.products
                    mproduct = products[1]
                    Log.d("OnSuccessOffering", mainOffering.products.toString())
                    for (product in mainOffering.products) {
//                        mproduct = product
                        Log.d("$$$$$$", product.storeDetails.toString())
                        Log.d(
                            "^^^^",
                            "then ${product.prettyPrice.orEmpty()} / ${
                                product.subscriptionPeriod?.unit.toString().orEmpty()
                            }"
                        )
                        Log.d("^^^^", product.trialPeriod.toString())
                        Log.d("^^^^", product.subscriptionPeriod.toString())
                        Log.d("&&&&&&&", getReadablePeriod(product.trialPeriod?.iso ?: ""))
                    }
                    //startPurchase(activity, onErrorPurchase = {
                    // })
                }
            }

            override fun onError(error: QonversionError) {
                Log.d("OnErrorOffering", error.toString())
            }
        })
    }

    fun getReadablePeriod(billingPeriod: String): String {
        if (billingPeriod.startsWith("P")) {
            val period = billingPeriod.substring(1)
            val number = period.substring(0, period.length - 1).toInt()
            val unit = period.last()

            return when (unit) {
                'D' -> "$number day${if (number > 1) "s" else ""} free"
                'W' -> "$number week${if (number > 1) "s" else ""} free"
                'M' -> "$number month${if (number > 1) "s" else ""} free"
                'Y' -> "$number year${if (number > 1) "s" else ""} free"
                else -> "Free trial period"
            }
        }
        return "Free trial period"
    }

    fun startPurchase(activity: Activity, onErrorPurchase: (String) -> Unit) {
        val purchaseModel = mproduct.toPurchaseModel("testoffering")
        //val product = products.find { it.qonversionID == "qonversion-premium6" }
        //val purchaseModel = product?.toPurchaseModel()
        if (mproduct == null) {
            onErrorPurchase("Product not found")
            return
        }
        if (purchaseModel != null) {
            Qonversion.shared.purchase(activity, purchaseModel, callback = object :
                QonversionEntitlementsCallback {
                override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                    val premiumEntitlement = entitlements[""]
                    if (premiumEntitlement != null && premiumEntitlement.isActive) {
                        Log.d("onSuccessPurchase", entitlements.toString())
                    }
                }

                override fun onError(error: QonversionError) {
                    onErrorPurchase(error.description)
                    if (error.code === QonversionErrorCode.ProductUnavailable) {
                        Log.d("onErrorPurchase", error.toString())
                    }
                }
            })
        }
    }

    fun checkSubscriptionChanges() {
        Qonversion.shared.userProperties(object : QonversionUserPropertiesCallback {
            override fun onError(error: QonversionError) {
                Log.d("$$$$$$$$", error.toString())
            }

            override fun onSuccess(userProperties: QUserProperties) {
                Log.d("^^^^^^^", userProperties.toString())
            }

        })
    }

    fun openSubscriptionManagement(context: Context) {
//        val packageName = context?.packageName ?: return
//        val url = "https://play.google.com/store/account/subscriptions?sku=package=$packageName"
//
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse(url)
//        if (intent.resolveActivity((context as Activity).packageManager) != null) {
//            (context as Activity).startActivity(intent)
//        } else {
//            Log.e("SubscriptionManagement", "No browser found to open the subscription management page.")
//        }
        Qonversion.shared.syncPurchases()
    }

    fun updatePurchase(activity: Activity, id: String) {
        //val purchaseUpdateModel = mproduct.toPurchaseUpdateModel("qonversion-premium7")
        val product = products.find { it.qonversionID == "qonversion-premium6" }
        val purchaseModel = product?.toPurchaseUpdateModel(id)

        if (purchaseModel != null) {
            Qonversion.shared.updatePurchase(
                activity,
                purchaseModel,
                callback = object : QonversionEntitlementsCallback {
                    override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                        val premiumEntitlement = entitlements["premium_access"]
                        if (premiumEntitlement != null && premiumEntitlement.isActive) {
                            Log.d("onSuccessUpdatePurchase", entitlements.toString())
                        }
                    }

                    override fun onError(error: QonversionError) {
                        Log.d("onErrorUpdatePurchase", error.toString())
                    }
                })
        }
    }

    fun restorePurchase() {
        Qonversion.shared.restore(object : QonversionEntitlementsCallback {
            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements["premium_access"]
                if (premiumEntitlement != null && premiumEntitlement.isActive) {
                    Log.d("onSuccessRestore", entitlements.toString())
                }
            }

            override fun onError(error: QonversionError) {
                Log.d("onErrorRestore", error.toString())
            }
        })
    }

    fun checkEntitlements(activity: Activity) {
        Qonversion.shared.checkEntitlements(object : QonversionEntitlementsCallback {
            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements["premium_access"]
                if (premiumEntitlement != null && premiumEntitlement.isActive) {
                    updatePurchase(activity, premiumEntitlement.productId)
                } else {
                    startPurchase(activity, onErrorPurchase = {
                    })
                }
            }

            override fun onError(error: QonversionError) {
                Log.d("onErrorCheckEntitlements", error.toString())
            }
        })
    }

    fun identify(userId: String) {
        Qonversion.shared.identify(userId)

        Qonversion.shared.identify(userId, object : QonversionUserCallback {
            override fun onSuccess(user: QUser) {
                Log.d("IdentifyUserSuccess", user.toString())
            }

            override fun onError(error: QonversionError) {
                Log.d("IdentifyUserError", error.toString())
            }
        })
    }

}
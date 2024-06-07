package com.hyperether.howlucky

import android.app.Activity
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
import com.qonversion.android.sdk.listeners.QonversionEntitlementsCallback
import com.qonversion.android.sdk.listeners.QonversionOfferingsCallback
import com.qonversion.android.sdk.listeners.QonversionUserCallback

class QonversionViewModel() : ViewModel() {

    val ENTITLEMENT_ID = "business_card_service_access"
    private lateinit var product: QProduct
    private val productDetails: MutableMap<String, ProductDetails> = HashMap()

    fun loadOffer(activity: Activity) {
        Qonversion.shared.offerings(object : QonversionOfferingsCallback {
            override fun onSuccess(offerings: QOfferings) {
                val mainOffering = offerings.main
                if (mainOffering != null && mainOffering.products.isNotEmpty()) {
                    Log.d("OnSuccessOffering", mainOffering.products.toString())
                    product = mainOffering.products.firstOrNull()!!
                    startPurchase(activity, onErrorPurchase = {
                    }
                    )
                }
            }

            override fun onError(error: QonversionError) {
                Log.d("OnErrorOffering", error.toString())
            }
        })
    }

    fun startPurchase(activity: Activity, onErrorPurchase: (String) -> Unit) {
        val purchaseModel = product.toPurchaseModel(product.offeringID)
        Qonversion.shared.purchase(activity, purchaseModel, callback = object :
            QonversionEntitlementsCallback {
            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements[ENTITLEMENT_ID]
                if (premiumEntitlement != null && premiumEntitlement.isActive) {
                    Log.d("onSuccessPurchase", entitlements.toString())
                }
            }

            override fun onError(error: QonversionError) {
                onErrorPurchase(error.description)
                if (error.code === QonversionErrorCode.PurchaseInvalid) {
                    Log.d("onErrorPurchase", error.toString())
                }
            }
        })
    }

    fun updatePurchase(activity: Activity) {
        val purchaseUpdateModel = product.toPurchaseUpdateModel("oldProductId")
        Qonversion.shared.updatePurchase(
            activity,
            purchaseUpdateModel,
            callback = object : QonversionEntitlementsCallback {
                override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                    val premiumEntitlement = entitlements[ENTITLEMENT_ID]
                    if (premiumEntitlement != null && premiumEntitlement.isActive) {
                        Log.d("onSuccessUpdatePurchase", entitlements.toString())
                    }
                }

                override fun onError(error: QonversionError) {
                    Log.d("onErrorUpdatePurchase", error.toString())
                }
            })
    }

    fun restorePurchase() {
        Qonversion.shared.restore(object : QonversionEntitlementsCallback {
            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements[ENTITLEMENT_ID]
                if (premiumEntitlement != null && premiumEntitlement.isActive) {
                    Log.d("onSuccessRestore", entitlements.toString())
                }
            }

            override fun onError(error: QonversionError) {
                Log.d("onErrorRestore", error.toString())
            }
        })
    }

    fun checkEntitlements() {
        Qonversion.shared.checkEntitlements(object : QonversionEntitlementsCallback {
            override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                val premiumEntitlement = entitlements[ENTITLEMENT_ID]
                if (premiumEntitlement != null && premiumEntitlement.isActive) {

                    when (premiumEntitlement.renewState) {
                        QEntitlementRenewState.NonRenewable -> {
                            // NonRenewable is the state of a consumable or non-consumable in-app purchase
                        }

                        QEntitlementRenewState.WillRenew -> {
                            // WillRenew is the state of an auto-renewable subscription
                        }

                        QEntitlementRenewState.BillingIssue -> {
                            // Prompt the user to update the payment method.
                        }

                        QEntitlementRenewState.Canceled -> {
                            // The user has turned off auto-renewal for the subscription, but the subscription has not expired yet.
                            // Prompt the user to resubscribe with a special offer.
                        }

                        else -> {

                        }
                    }
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
package com.hyperether.howlucky

import android.content.Context
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionConfig
import com.qonversion.android.sdk.dto.QEnvironment
import com.qonversion.android.sdk.dto.QLaunchMode
import com.qonversion.android.sdk.dto.QUser
import com.qonversion.android.sdk.dto.QonversionError
import com.qonversion.android.sdk.listeners.QonversionUserCallback

object SubscriptionManager {

    var userId: String? = null

    fun initialize(context: Context) {
        val qonversionConfig = QonversionConfig.Builder(
            context,
            BuildConfig.QONVERSION_API_KEY,
            QLaunchMode.SubscriptionManagement
        )
            .setEnvironment(QEnvironment.Sandbox)
            .build()

        Qonversion.initialize(qonversionConfig)
        Qonversion.shared.userInfo(object : QonversionUserCallback {
            override fun onSuccess(user: QUser) {
                userId = user.qonversionId
            }

            override fun onError(error: QonversionError) {
                // handle error here
            }
        })
        Qonversion.shared.syncHistoricalData()
    }

    fun qonversionLogout() {
        Qonversion.shared.logout()
    }
}
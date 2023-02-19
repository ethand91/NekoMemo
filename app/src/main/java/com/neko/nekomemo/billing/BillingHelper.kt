package com.neko.nekomemo.billing

import android.app.Activity
import com.android.billingclient.api.*
import com.neko.nekomemo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BillingHelper(
    val activity: Activity
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private lateinit var billingClient: BillingClient
    private lateinit var productDetails: ProductDetails
    private lateinit var purchase: Purchase

    private val productId = "remove_ads"

    private val _productName = MutableStateFlow(activity.getString(R.string.searching_products))
    val productName = _productName.asStateFlow()

    private val _buyEnabled = MutableStateFlow(false)
    val buyEnabled = _buyEnabled.asStateFlow()

    private val _consumeEnabled = MutableStateFlow(false)
    val consumeEnabled = _consumeEnabled.asStateFlow()

    private val _isConsumed = MutableStateFlow(false)
    val isConsumed = _isConsumed.asStateFlow()

    private val _statusText = MutableStateFlow(activity.getString(R.string.billing_init))
    val statusText = _statusText.asStateFlow()

    fun billingSetup() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingSetupFinished(
                billingResult: BillingResult
            ) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _statusText.value = activity.getString(R.string.billing_connection_ok)
                    queryProduct(productId)
                    reloadPurchase()
                } else {
                    _statusText.value = activity.getString(R.string.billing_connection_failed)
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusText.value =  activity.getString(R.string.billing_connection_disconnect)
            }
        })
    }

    fun queryProduct(productId: String) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                mutableListOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(
                            BillingClient.ProductType.INAPP
                        )
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { _, productDetailsList ->
            if (productDetailsList.isNotEmpty()) {
                productDetails = productDetailsList[0]
                _productName.value = "Product: ${productDetails.name}"
            } else {
                _statusText.value = activity.getString(R.string.no_products)
                _buyEnabled.value = false
            }
        }
    }

    fun makePurchase() {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                mutableListOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun consumePurchase() {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        scope.launch {
            val result = billingClient.consumePurchase(consumeParams)

            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _statusText.value = activity.getString(R.string.purchase_consumed)
                _buyEnabled.value = false
                _consumeEnabled.value = false
                _isConsumed.value = true
            }
        }
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                completePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _statusText.value = activity.getString(R.string.purchase_cancelled)
        } else {
            _statusText.value = activity.getString(R.string.purchase_error)
        }
    }

    private fun completePurchase(item: Purchase) {
        purchase = item

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _buyEnabled.value = false
            _consumeEnabled.value = true
            _statusText.value = activity.getString(R.string.purchase_complete)
            _isConsumed.value = true
        }
    }

    private fun reloadPurchase() {
        val queryPurchaseParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(
            queryPurchaseParams,
            purchaseListener
        )
    }

    private val purchaseListener = PurchasesResponseListener { _, purchases ->
        if (purchases.isNotEmpty()) {
            purchase = purchases.first()
            _buyEnabled.value = false
            _consumeEnabled.value = false
            _isConsumed.value = true
        } else {
            _buyEnabled.value = true
            _consumeEnabled.value = false
        }
    }
}
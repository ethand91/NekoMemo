package com.neko.nekomemo.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.neko.nekomemo.R

@SuppressLint("VisibleForTests")
@Composable
fun FullScreenAdView(
    isDebug: Boolean = true,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val unitId = if (isDebug) context.getString(R.string.video_test_ad)
        else context.getString(R.string.video_ad)

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            AdView(context).apply {
                adUnitId = unitId
                adListener = object: AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }

                    override fun onAdClosed() {
                        super.onAdClosed()
                        onClose()
                    }
                }
                setAdSize(AdSize.FULL_BANNER)
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}
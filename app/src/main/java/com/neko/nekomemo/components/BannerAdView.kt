package com.neko.nekomemo.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.neko.nekomemo.R

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            val adView = AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.test_ad)
                loadAd(AdRequest.Builder().build())
            }

            adView
        }
    )
}
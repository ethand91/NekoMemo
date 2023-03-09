package com.neko.nekomemo.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.neko.nekomemo.R

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView(
    isDebug: Boolean = false
) {
    val unitId = if (isDebug) stringResource(id = R.string.test_ad)
        else stringResource(id = R.string.bottom_ad)


    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = unitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
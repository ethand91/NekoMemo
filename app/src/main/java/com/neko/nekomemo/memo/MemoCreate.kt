package com.neko.nekomemo.memo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.ads.*
import com.neko.nekomemo.R
import com.neko.nekomemo.billing.BillingHelper
import com.neko.nekomemo.components.BannerAdView
import com.neko.nekomemo.components.NekoOutlinedTextField
import com.neko.nekomemo.components.NekoTopAppBar
import com.neko.nekomemo.db.Memo
import com.neko.nekomemo.db.MemoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.neko.nekomemo.components.FullScreenAdView

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MemoCreate(
    dao: MemoDao,
    navController: NavController,
    id: Int?,
    onUpdateList: () -> Unit = {},
    billingHelper: BillingHelper,
    activity: Activity
) {
    val scope = MainScope()
    val context = LocalContext.current
    val memo = remember {
        mutableStateOf(Memo())
    }
    val showAds = billingHelper.isConsumed.collectAsState(false)
    var interstitialAd: InterstitialAd? = null
    val unitId = context.getString(R.string.video_test_ad)

    val title = remember {
        mutableStateOf(TextFieldValue(memo.value.title))
    }
    val body = remember {
        mutableStateOf(TextFieldValue(memo.value.body))
    }
    val isAdShow = remember {
        mutableStateOf(false)
    }

    @SuppressLint("VisibleForTests")
    fun loadAd() {
        InterstitialAd.load(
            context,
            unitId,
            AdRequest.Builder().build(),
            object: InterstitialAdLoadCallback () {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    interstitialAd = null
                    Log.e("Memo", p0.message)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    Log.d("Memo", "ad loaded" + p0.toString())
                    interstitialAd = p0
                }
            }
        )
    }

    fun showAd(
        activity: Activity,
        onAdDismissed: () -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.Main) {
                Log.d("Memo", "Show Ad " + interstitialAd.toString())
                if (interstitialAd != null) {
                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            interstitialAd = null

                            Log.e("Memo", p0.message)
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            interstitialAd = null

                            loadAd()
                            onAdDismissed()
                            Log.d("Memo", "Ad Showed")
                        }
                    }

                    interstitialAd?.show(activity)
                }
            }
        }

        onAdDismissed()
    }

    fun removeAd() {
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
    }

    LaunchedEffect(key1 = Unit, block = {
        scope.launch {
            withContext(Dispatchers.Default) {
                val previousMemo = dao.getMemoById(id)

                if (previousMemo != null) {
                    memo.value = previousMemo
                    title.value = TextFieldValue(previousMemo.title)
                    body.value = TextFieldValue(previousMemo.body)
                }
            }
        }

        withContext(Dispatchers.Main) {
            loadAd()
        }
    })

    Column {
        NekoTopAppBar(
            navController = navController,
            title = stringResource(id = R.string.main_title),
            showBackIcon = true
        )

        Column(modifier = Modifier.weight(1f)) {
            NekoOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title.value,
                onValueChange = {
                    title.value = it
                },
                singleLine = true,
                label = stringResource(id = R.string.memo_title)
            )

            NekoOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = body.value,
                onValueChange = { body.value = it },
                label = stringResource(id = R.string.memo_body)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (title.value.text.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.memo_no_title), Toast.LENGTH_SHORT).show()

                        return@Button
                    }

                    scope.launch {
                        withContext(Dispatchers.Default) {
                            if (memo.value.title.isEmpty()) {
                                dao.insert(Memo(
                                    title = title.value.text,
                                    body = body.value.text
                                ))
                            } else {
                                memo.value.title = title.value.text
                                memo.value.body = body.value.text

                                dao.updateMemo(memo.value)
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, context.getString(R.string.memo_saved), Toast.LENGTH_SHORT).show()
                                navController.navigate("/main")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.memo_save),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (isAdShow.value) {
            FullScreenAdView(
                isDebug = true,
                onClose = {
                    isAdShow.value = false
                    navController.navigate("/main")
                }
            )
        }

        if (!showAds.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                BannerAdView()
            }
        }
    }
}
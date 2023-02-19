package com.neko.nekomemo.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neko.nekomemo.R
import com.neko.nekomemo.billing.BillingHelper
import com.neko.nekomemo.components.NekoTopAppBar

@Composable
fun RemoveAdvertisement(
    navController: NavController,
    billingHelper: BillingHelper
) {
    val buyEnabled by billingHelper.buyEnabled.collectAsState(false)
    val productName by billingHelper.productName.collectAsState("")
    val statusText by billingHelper.statusText.collectAsState("")

    NekoTopAppBar(
        navController = navController,
        title = stringResource(id = R.string.remove_ad_title),
        showBackIcon = true
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = stringResource(id = R.string.remove_ad_line_1),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.remove_ad_line_2),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = productName,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = statusText,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                billingHelper.makePurchase()
            },
            enabled = buyEnabled
        ) {
            Text(
                text = stringResource(id = R.string.remove_ad_button),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
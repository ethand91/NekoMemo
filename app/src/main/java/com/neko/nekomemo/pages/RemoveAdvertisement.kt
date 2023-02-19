package com.neko.nekomemo.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neko.nekomemo.R
import com.neko.nekomemo.components.NekoTopAppBar

@Composable
fun RemoveAdvertisement(
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NekoTopAppBar(
            navController = navController,
            title = stringResource(id = R.string.remove_ad_title),
            showBackIcon = true
        )
        
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

        Button(
            onClick = {

            }
        ) {
            Text(
                text = stringResource(id = R.string.remove_ad_button),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
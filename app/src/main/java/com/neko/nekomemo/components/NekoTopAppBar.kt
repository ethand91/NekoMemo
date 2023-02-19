package com.neko.nekomemo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

@Composable
fun NekoTopAppBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
) {
    val displayMenu = remember {
        mutableStateOf(false)
    }

    TopAppBar(
        title = { Text(text = title) },
        backgroundColor = Color.Black,
        contentColor = Color.White,
        navigationIcon = if (showBackIcon && navController.previousBackStackEntry != null) {
            {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        } else { null },
        actions = {
            IconButton(
                onClick = { displayMenu.value = !displayMenu.value }
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = ""
                )

                DropdownMenu(
                    expanded = displayMenu.value,
                    onDismissRequest = { displayMenu.value = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            navController.navigate("/remove-ad")
                        }
                    ) {
                        Text(
                            text = stringResource(id = com.neko.nekomemo.R.string.top_bar_advertisement)
                        )
                    }
                }
            }
        }
    )
}
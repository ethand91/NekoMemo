package com.neko.nekomemo.memo

import android.util.Log
import androidx.compose.animation.AnimatedContentScope.SlideDirection.Companion.End
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neko.nekomemo.R
import com.neko.nekomemo.db.Memo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MemoItem(
    memo: Memo,
    onClick: (memo: Memo) -> Unit = {},
    onDelete: (memo: Memo) -> Unit = {}
) {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN)
    val expanded = remember {
        mutableStateOf(false)
    }
    val showDeleteDialog = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClick(memo)
            }
    ) {
        Text(
            text = memo.title,
            modifier = Modifier
                .fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${stringResource(id = R.string.memo_created_at)} ${sdf.format(memo.created_at)}",
                fontSize = 12.sp,
                color = Color.LightGray,
            )

            IconButton(
                onClick = { expanded.value = true },
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = ""
                )
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        ) {

            DropdownMenuItem(
                onClick = {
                    expanded.value = false
                    showDeleteDialog.value = true
                }
            ) {
                Text(text = stringResource(id = R.string.memo_delete))
            }
        }

        if (showDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog.value = false },
                title = {
                    Text(text = stringResource(id = R.string.memo_alert_title))
                },
                text = {
                    Text(text = "${memo.title}${stringResource(id = R.string.memo_alert_text)}")
                },
                confirmButton = {
                    Button(
                        onClick = { onDelete(memo) }
                    ) {
                        Text(text = stringResource(id = R.string.memo_alert_confirm))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteDialog.value = false }
                    ) {
                        Text(text = stringResource(id = R.string.memo_alert_cancel))
                    }
                }
            )
        }
    }
}
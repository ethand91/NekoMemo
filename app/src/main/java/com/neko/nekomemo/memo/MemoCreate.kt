package com.neko.nekomemo.memo

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.neko.nekomemo.R
import com.neko.nekomemo.components.NekoOutlinedTextField
import com.neko.nekomemo.components.NekoTopAppBar
import com.neko.nekomemo.db.Memo
import com.neko.nekomemo.db.MemoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MemoCreate(
    dao: MemoDao,
    navController: NavController,
    id: Int?,
    onUpdateList: () -> Unit = {}
) {
    val scope = MainScope()
    val context = LocalContext.current
    val memo = remember {
        mutableStateOf(Memo())
    }

    val title = remember {
        mutableStateOf(TextFieldValue(memo.value.title))
    }
    val body = remember {
        mutableStateOf(TextFieldValue(memo.value.body))
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

                                onUpdateList()
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
    }
}
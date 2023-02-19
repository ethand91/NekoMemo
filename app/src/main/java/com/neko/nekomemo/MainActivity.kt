package com.neko.nekomemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.neko.nekomemo.components.BannerAdView
import com.neko.nekomemo.components.NekoSearchBar
import com.neko.nekomemo.components.NekoTopAppBar
import com.neko.nekomemo.db.Memo
import com.neko.nekomemo.memo.MemoCreate
import com.neko.nekomemo.memo.MemoItem
import com.neko.nekomemo.pages.RemoveAdvertisement
import com.neko.nekomemo.ui.theme.NekoMemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val dao = MainApplication.database.memoDao()

    private var memoList = mutableStateListOf<Memo>()
    private var filteredList = mutableStateListOf<Memo>()
    private var scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NekoMemoTheme {
                LaunchedEffect(key1 = true, block = {
                    loadMemos()
                })

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainNavigation(memoList = memoList)
                }
            }
        }
    }

    private fun loadMemos() {
        Log.d("Main", "loadMemos")
        scope.launch {
            withContext(Dispatchers.Default) {
                dao.getAll().forEach{ memo ->
                    memoList.add(memo)
                }
            }
        }
    }

    private fun deleteMemo(memo: Memo) {
        scope.launch {
            withContext(Dispatchers.Default) {
                dao.delete(memo)

                memoList.clear()
                loadMemos()
            }
        }
    }

    private fun deleteAll() {
        scope.launch {
            withContext(Dispatchers.Default) {
                dao.deleteAll()

                memoList.clear()
            }
        }
    }

    @Composable
    fun MainScreen(
        navController: NavController,
        memoList: SnapshotStateList<Memo>
    ) {
        val context = LocalContext.current

        Column {
            NekoTopAppBar(
                navController = navController,
                title = stringResource(id = R.string.main_title),
                showBackIcon = false
            )

            /*
            NekoSearchBar(onSearch = {
                filteredList.addAll(memoList.filter { memo ->
                    memo.title.lowercase().contains(it.lowercase())
                })
            })
             */

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)) {

                items(memoList) { memo ->
                    key(memo.id) {
                        MemoItem(
                            memo = memo,
                            onClick = {
                                navController.navigate("/memo/${it.id}")
                            },
                            onDelete = {
                                deleteMemo(it)
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        navController.navigate("/memo")
                    },
                    modifier = Modifier.background(Color.Blue, RoundedCornerShape(30.dp))
                ) {
                     Icon(
                         Icons.Filled.Add,
                         contentDescription = "",
                         tint = Color.White,
                     )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                BannerAdView()
            }
        }
    }

    @Composable
    fun MainNavigation(memoList: SnapshotStateList<Memo>) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "/main") {
            composable("/main") {
                MainScreen(
                    navController = navController,
                    memoList = memoList
                )
            }

            composable("/memo") {
                MemoCreate(
                    dao = dao,
                    navController = navController,
                    id = null,
                    onUpdateList = {
                        memoList.clear()
                        loadMemos()
                    }
                )
            }

            composable("/memo/{id}", arguments = listOf(navArgument("id") {
                type = NavType.IntType
            })) {
                MemoCreate(
                    id = it.arguments?.getInt("id"),
                    dao = dao,
                    navController = navController,
                    onUpdateList = {
                        memoList.clear()
                        loadMemos()
                    }
                )
            }

            composable("/remove-ad") {
                RemoveAdvertisement(navController = navController)
            }
        }
    }
}
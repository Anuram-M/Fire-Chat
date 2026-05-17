package com.ram.firechat.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ram.firechat.R
import com.ram.firechat.ui.theme.almostBlack
import com.ram.firechat.ui.theme.firaFamily
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.UXUtil
import com.ram.firechat.viewmodel.FireViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatComposable(navController: NavController, viewModel: FireViewModel) {

    val user by viewModel.otherUser.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val chatmessage by viewModel.chatMessages.collectAsState()
    val context = LocalContext.current.applicationContext
    val grouped = chatmessage
        .sortedBy { it.time }
        .groupBy { it.dateStamp }
    val colors = UXUtil.getColors()
    var txt by remember {
        mutableStateOf("")
    }
    val avatar = when {
        user?.avatarChoice == 1 -> R.drawable.satisfied256
        user?.avatarChoice == 2 -> R.drawable.icecrystal256
        user?.avatarChoice == 3 -> R.drawable.mask256
        else -> R.drawable.brightness256
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(chatmessage) {
        if (chatmessage.isNotEmpty()) {
            listState.animateScrollToItem(chatmessage.size - 1)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(almostBlack)
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(colors.primaryAccentColor)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape
                ) {
                    Image(
                        painter = painterResource(avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                }
                Text(
                    text = user?.userName.toString(),
                    fontFamily = firaFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
                    .background(colors.primaryAccentColor),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.hasFocus) {
                                    coroutineScope.launch {
                                        if (chatmessage.isNotEmpty()) {
                                            listState.animateScrollToItem(chatmessage.size - 1)
                                        }
                                    }
                                }
                            }
                            .background(Color.Transparent)
                            .weight(1f),
                        value = txt,
                        onValueChange = {
                            txt = it
                        },
                        maxLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = colors.textColor,
                            unfocusedTextColor = colors.textColor,
                            focusedContainerColor = colors.primaryAccentColor,
                            unfocusedContainerColor = colors.primaryAccentColor,
                            focusedIndicatorColor = colors.primaryAccentColor,
                            unfocusedIndicatorColor = colors.primaryAccentColor
                        ),
                        placeholder = {
                            Text(
                                text = "Enter message here",
                                color = Color.LightGray
                            )
                        },
                    )
                    IconButton(
                        onClick = {
                            FireUtil.sendMsg(context, txt, listOf(currentUser, user))
                            txt = ""
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(colors.textColor, shape = CircleShape)
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            modifier = Modifier.padding(start = 5.dp),
                            contentDescription = null,
                            tint = colors.primaryAccentColor
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(colors.secondaryAccentColor)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                        .weight(1f),
                ) {
                    grouped.forEach { (t, msgs) ->
                        item(key = t) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = colors.primaryAccentColor
                                    )
                                ) {
                                    Text(
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 4.dp
                                        ),
                                        text = t,
                                        color = colors.textColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        items(msgs, key = { it.time }) {
                            Column {
                                if (it.senderId != currentUser?.uid) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Card(
                                            modifier = Modifier.padding(
                                                start = 10.dp,
                                                top = 3.dp,
                                                bottom = 3.dp,
                                                end = 60.dp
                                            ),
                                            shape = RoundedCornerShape(
                                                topEnd = 10.dp,
                                                topStart = 10.dp,
                                                bottomEnd = 10.dp
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = colors.textColor
                                            )
                                        ) {
                                            Box {
                                                Text(
                                                    text = it.text,
                                                    fontSize = 14.sp,
                                                    color = colors.primaryAccentColor,
                                                    modifier = Modifier
                                                        .padding(
                                                            end = 60.dp,
                                                            top = 6.dp,
                                                            bottom = 6.dp,
                                                            start = 10.dp
                                                        )
                                                )
                                                Text(
                                                    text = it.timeStamp,
                                                    fontSize = 10.sp,
                                                    color = colors.primaryAccentColor,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(end = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Card(
                                            modifier = Modifier.padding(
                                                start = 60.dp,
                                                top = 3.dp,
                                                bottom = 3.dp,
                                                end = 10.dp
                                            ),
                                            shape = RoundedCornerShape(
                                                topEnd = 10.dp,
                                                topStart = 10.dp,
                                                bottomStart = 10.dp
                                            ),
                                            colors = CardDefaults.cardColors(
                                                containerColor = colors.primaryAccentColor
                                            )
                                        ) {
                                            Box {
                                                Text(
                                                    text = it.text,
                                                    fontSize = 14.sp,
                                                    color = colors.textColor,
                                                    modifier = Modifier
                                                        .padding(
                                                            end = 60.dp,
                                                            top = 6.dp,
                                                            bottom = 6.dp,
                                                            start = 10.dp
                                                        )
                                                )
                                                Text(
                                                    text = it.timeStamp,
                                                    fontSize = 10.sp,
                                                    color = colors.textColor,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(end = 5.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
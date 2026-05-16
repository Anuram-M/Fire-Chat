package com.ram.firechat.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.ram.firechat.R
import com.ram.firechat.model.UserModel
import com.ram.firechat.ui.theme.firaFamily
import com.ram.firechat.util.UXUtil
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.PreferenceUtil
import com.ram.firechat.viewmodel.FireViewModel

@Composable
fun HomeComposable(navController: NavHostController, viewModel: FireViewModel) {
    val users by viewModel.users.collectAsState()

    val chats by viewModel.chats.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()

    val quote by viewModel.quoteOfTheDay.collectAsState()

    val context = LocalContext.current.applicationContext
    LaunchedEffect(Unit) {
        PreferenceUtil.putString(context, "screen_state", "home")
    }
    val colors = UXUtil.getColors()
    Scaffold(
        modifier = Modifier
            .background(colors.primaryAccentColor)
            .systemBarsPadding(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 10.dp, spotColor = Color.Black),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryAccentColor
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.primaryAccentColor)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FireChat",
                            color = colors.textColor,
                            fontFamily = firaFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        IconButton(
                            onClick = {
                                FireUtil.signOut(navController)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.logout),
                                modifier = Modifier.size(28.dp),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.primaryAccentColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp)
            ) {
                LazyRow {
                    items(users) { user ->
                        val avatar = when {
                            (user.avatarChoice == 1) -> R.drawable.satisfied256
                            user.avatarChoice == 2 -> R.drawable.icecrystal256
                            user.avatarChoice == 3 -> R.drawable.mask256
                            else -> R.drawable.brightness256
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Card(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        val chatid = listOf(
                                            FirebaseAuth.getInstance().currentUser?.uid.toString(),
                                            user.uid
                                        ).sorted().joinToString("_")
                                        viewModel.updateChatItems(user)
                                        viewModel.setChatId(chatid)
                                        navController.navigate("chat")
                                    }
                                    .padding(10.dp),
                                shape = CircleShape
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(avatar),
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }


                            }
                            Text(
                                text = user.userName,
                                maxLines = 1,
                                fontFamily = firaFamily,
                                modifier = Modifier.width(80.dp),
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (quote != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(shape = RoundedCornerShape(10.dp), color = Color.Black)
                                .padding(10.dp)
                            , contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Today's Quote",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    color = colors.textColor,
                                    fontFamily = firaFamily,
                                    fontSize = 18.sp
                                )
                                Row(
                                    modifier = Modifier.scrollable(
                                        rememberScrollState(),
                                        orientation = Orientation.Horizontal
                                    )
                                ) {
                                    Text(
                                        text = "\" ${quote} \"",
                                        fontSize = 16.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = colors.textColor,
                                        fontFamily = firaFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic
                                    )
                                }

                            }
                        }
                    }
                }
                if (chats.isNotEmpty())
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        items(chats) { chat ->
                            var avatar = when {
                                chat.avatarChoice == 1 -> R.drawable.satisfied256
                                chat.avatarChoice == 2 -> R.drawable.icecrystal256
                                chat.avatarChoice == 3 -> R.drawable.mask256
                                else -> R.drawable.brightness256
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        viewModel.updateChatItems(
                                            UserModel(
                                                userName = chat.userName,
                                                avatarChoice = chat.avatarChoice,
                                                uid = chat.receiverUserId,
                                                fcmToken = ""
                                            )
                                        )
                                        viewModel.setChatId(chatId = chat.chatId)
                                        navController.navigate("chat")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(avatar),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(Color.Black, shape = CircleShape),
                                    contentDescription = null
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = chat.userName,
                                        fontSize = 15.sp,
                                        fontFamily = firaFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.secondaryAccentColor
                                    )
                                    Text(
                                        text = chat.lastMessage,
                                        fontSize = 12.sp,
                                        fontFamily = firaFamily,
                                        color = colors.secondaryAccentColor,
                                        fontWeight = FontWeight.Light
                                    )
                                }
                            }

                        }
                    }
                else
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "No Chats available!"
                        )
                    }
            }

        }
    }

}
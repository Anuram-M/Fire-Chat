package com.ram.firechat.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ram.firechat.model.AvatarModel
import com.ram.firechat.ui.theme.FireChatTheme
import com.ram.firechat.ui.theme.firaFamily
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.PreferenceUtil
import com.ram.firechat.util.UXUtil

@Composable
fun SetUserNameComposable(navController: NavController) {
    var userName by remember {
        mutableStateOf("Unknown")
    }
    val maxCount = 15
    var enteredCount by remember {
        mutableStateOf(userName.length)
    }
    val avatars = UXUtil.getAvatarList()
    val columnCount = minOf(avatars.size, 4)
    val colors = UXUtil.getColors()

    var selectedAvatar by remember {
        mutableStateOf<AvatarModel>(avatars[0])
    }
    val context = LocalContext.current.applicationContext
    LaunchedEffect(Unit) {
        PreferenceUtil.putString(context, "screen_state", "set-username")
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primaryAccentColor),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
            ) {
                Text(
                    text = "Set Username",
                    color = colors.textColor,
                    fontFamily = firaFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.primaryAccentColor)
//                .systemBarsPadding()
                .padding(bottom = 50.dp), contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Row(modifier = Modifier.padding(20.dp)) {
                    TextButton (onClick = {

                        FireUtil.signOut(navController)
                    }) {
                        Text(
                            text = "Sign-Out",
                            color = colors.textColor
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(20.dp)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.textColor
                        ),
                        onClick = {
                           FireUtil.updateUser(navController, selectedAvatar, userName)
                    }) {
                        Text(
                            text = "Confirm",
                            color = colors.primaryAccentColor
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = colors.textColor,
                        shape = CircleShape
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.primaryAccentColor
                    )
                ) {
                    Image(
                        painter = painterResource(selectedAvatar.avatar),
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = userName,
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            if (it.length <= maxCount) {
                                userName = it
                                enteredCount = userName.length
                            }
                        },
                        textStyle = TextStyle(
                            fontFamily = firaFamily,
                        ),
                        label = {
                            Text(
                                text = ""
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Set your username here",
                                fontFamily = firaFamily,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colors.primaryAccentColor,
                            unfocusedContainerColor = colors.primaryAccentColor,
                            focusedTextColor = colors.textColor,
                            unfocusedTextColor = colors.textColor,
                            focusedIndicatorColor = colors.textColor,
                            unfocusedIndicatorColor = colors.textColor
                        )
                    )
                    Text(
                        text = "${enteredCount}/${maxCount}",
                        modifier = Modifier.padding(top = 10.dp),
                        color = colors.textColor,
                        fontFamily = firaFamily,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                )
                Text(
                    text = "Available Avatars",
                    fontSize = 14.sp,
                    fontFamily = firaFamily,
                    color = colors.textColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    avatars.forEach { item ->
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    selectedAvatar = item
                                }) {
                            Card(
                                shape = CircleShape,
                                modifier = Modifier
                                    .border(
                                        width = if (item == selectedAvatar) 3.dp else 1.dp,
                                        color = colors.textColor,
                                        shape = CircleShape
                                    )
                                    .size(60.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.primaryAccentColor
                                )
                            ) {
                                Image(
                                    painter = painterResource(item.avatar),
                                    modifier = Modifier
                                        .padding(6.dp),
                                    contentScale = ContentScale.FillWidth,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun userPreview() {
    FireChatTheme {
        SetUserNameComposable(rememberNavController())
    }
}
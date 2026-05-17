package com.ram.firechat.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ram.firechat.ui.theme.firaFamily
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.PreferenceUtil
import com.ram.firechat.util.UXUtil
import com.ram.firechat.viewmodel.FireViewModel

@Composable
fun AuthComposable(
    modifier: Modifier,
    navController: NavHostController,
    fireViewModel: FireViewModel
) {
    var userName by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var isLogin by rememberSaveable {
        mutableStateOf(true)
    }
    val colors = UXUtil.getColors()
    val context = LocalContext.current.applicationContext
    LaunchedEffect(Unit) {
        PreferenceUtil.putString(context, "screen_state", "login")
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primaryAccentColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {

        }
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = if(isLogin) "LOGIN" else "SIGNUP",
                    fontFamily = firaFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    style = TextStyle(
                        letterSpacing = TextUnit(5.0f, TextUnitType.Sp)
                    ),
                    color = colors.textColor
                )

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                )
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    value = userName,
                    onValueChange = {
                        userName = it
                    },
                    label = {
                        Text(
                            text = "Email",
                            color = colors.textColor
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = colors.textColor
                    ),
                    placeholder = {
                        Text(
                            text = "Enter your email Here",
                            color = Color.LightGray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.primaryAccentColor,
                        unfocusedContainerColor = colors.primaryAccentColor,
                        focusedIndicatorColor = colors.textColor,
                        unfocusedIndicatorColor = colors.textColor,
                    )
                )
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        Text(
                            text = "Password",
                            color = colors.textColor
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = colors.textColor
                    ),
                    placeholder = {
                        Text(
                            text = "Enter password here",
                            color = Color.LightGray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.primaryAccentColor,
                        unfocusedContainerColor = colors.primaryAccentColor,
                        focusedIndicatorColor = colors.textColor,
                        unfocusedIndicatorColor = colors.textColor,
                    )
                )

                TextButton(onClick = {
                    isLogin = !isLogin
                }) {
                    Text(
                        text = if(isLogin) "Don't have an account? Signup" else "Already have an account? Login",
                        color = colors.textColor,
                        fontSize = 15.sp
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.textColor,
                        contentColor = colors.primaryAccentColor
                    ),
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        if(isLogin) {
                            FireUtil.loginUser(userName, password, navController)
                        } else {

                            FireUtil.createUser(userName, password, navController)
                        }
                    }) {
                    Text(
                        text = if(isLogin) "LOGIN" else "SIGNUP",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
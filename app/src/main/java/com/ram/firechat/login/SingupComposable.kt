package com.ram.firechat.login

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ram.firechat.ui.theme.FireChatTheme
import com.ram.firechat.ui.theme.firaFamily
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.UXUtil

@Composable
fun SignupComposable(modifier: Modifier, navController: NavController) {
    var userName by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    var confirmPassword by remember {
        mutableStateOf("")
    }
    val colors = UXUtil.getColors()
    val context = LocalContext.current.applicationContext
    Box(modifier = Modifier.fillMaxSize().background(colors.primaryAccentColor), contentAlignment = Alignment.Center) {
        Box(modifier= Modifier.fillMaxSize().systemBarsPadding(), contentAlignment = Alignment.BottomCenter) {
            TextButton(onClick = {
                userName = ""
                password = ""
                confirmPassword = ""
                navController.navigate("login") {
                    popUpTo(0)
                }
            }) {
               Text(
                   text = "Already have an account? Login here",
                   color = colors.textColor,
                   fontSize = 15.sp
               )
            }
        }
        Box {
            Column(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

                Text(
                    text = "SIGNUP",
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
                            text = "Username",
                            color = colors.textColor
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = colors.textColor
                    ),
                    placeholder = {
                        Text(
                            text = "UserName/Email Here",
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
                        fontSize = 15.sp,
                        color = colors.textColor
                    ),
                    placeholder = {
                        Text(
                            text = "Choose a strong password",
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(20.dp),
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    },
                    label = {
                        Text(
                            text = "Confirm Password",
                            color = colors.textColor
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = colors.textColor
                    ),
                    placeholder = {
                        Text(
                            text = "Re-Enter the password",
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
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.textColor,
                        contentColor = colors.primaryAccentColor
                    ),
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        if(password == confirmPassword) {
                            Toast.makeText(
                                context,
                                "Set creds are \n${userName}, ${password}",
                                Toast.LENGTH_SHORT
                            ).show()
                            FireUtil.createUser(userName, password, navController)
                        }
                    }) {
                    Text(
                        text = "SIGNUP",
                        fontSize = 14.sp
                    )
                }


            }


        }

    }
}

@Preview
@Composable
fun previewSignUp() {
    FireChatTheme {
        SignupComposable(Modifier, rememberNavController())
    }
}
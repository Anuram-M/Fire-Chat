package com.ram.firechat

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ram.firechat.chat.ChatComposable
import com.ram.firechat.home.HomeComposable
import com.ram.firechat.login.AuthComposable
import com.ram.firechat.login.SetUserNameComposable
import com.ram.firechat.util.PreferenceUtil
import com.ram.firechat.viewmodel.FireViewModel

@Composable
fun MainComposable(modifier: Modifier, fireViewModel: FireViewModel) {
    val navController = rememberNavController()
    val fireUser = FirebaseAuth.getInstance().currentUser
    var start by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current.applicationContext
    val screenState = PreferenceUtil.getString(context, "screen_state")
    start = when(screenState) {
        "login" -> "login"
        "set-username" -> "set-username"
        "home" -> "home"
        else -> "login"
    }



    Surface {
        NavHost(navController = navController, startDestination = start) {
            composable("login"){ AuthComposable(Modifier, navController, fireViewModel) }
            composable("home"){ HomeComposable(navController, fireViewModel) }
            composable("chat"){ ChatComposable(navController, fireViewModel) }
            composable("set-username") { SetUserNameComposable(navController) }
        }
    }
}
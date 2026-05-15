package com.ram.firechat

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.ram.firechat.ui.theme.FireChatTheme
import com.ram.firechat.util.DataStoreUtil
import com.ram.firechat.util.FireUtil
import com.ram.firechat.util.ProtoHandler
import com.ram.firechat.viewmodel.FireViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    lateinit var fireAuth: FirebaseAuth
    val fireViewModel: FireViewModel by viewModels()
    val notificationLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {result ->
        if(result.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false)) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        fireAuth = FirebaseAuth.getInstance()
        val saverUtil = DataStoreUtil(applicationContext)
        val protoSaver = ProtoHandler(applicationContext)
        lifecycleScope.launch {
            saverUtil.saveDate("user", "pass")
            val data = UserSettings.newBuilder().setUserName("name").setAccessLevel(3).setIsLoggedIn(false).build()
            protoSaver.saveData(data)
        }
        setContent {
            FireChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainComposable(Modifier.padding(innerPadding), fireViewModel)
                }
            }
        }
        lifecycleScope.launch {
//            saverUtil.userName.collectLatest {
//                Log.d("DATASTVED", "onCreate: $it")
//            }

            protoSaver.savedData.collectLatest {
                Log.d("PROTODST", "onCreate: ${it}")
            }
        }
        fetchRemoteConfig()
        notificationLauncher.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        )
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.d("TOKEN", "onCreate: ${task.result}")
            }
        }
    }

    fun fetchRemoteConfig() {
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener { info ->
            if(info.isSuccessful){
                fireViewModel.setTodaysQuote(FirebaseRemoteConfig.getInstance().getString("quote_of_the_day"))
                Log.d("REMOTE", "fetchRemoteConfig: ${info}")
                Log.d("REMOTE", "fetchRemoteConfig boolean : ${FirebaseRemoteConfig.getInstance().getBoolean("show_new_ui")}")
                Log.d("REMOTE", "fetchRemoteConfig boolean : ${FirebaseRemoteConfig.getInstance().getString("quote_of_the_day")}")
            }
        }

        FirebaseRemoteConfig.getInstance().addOnConfigUpdateListener(object : ConfigUpdateListener{
            override fun onUpdate(configUpdate: ConfigUpdate) {
                fireViewModel.setTodaysQuote(FirebaseRemoteConfig.getInstance().getString("quote_of_the_day"))
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                //no need for now
            }

        })
    }
}
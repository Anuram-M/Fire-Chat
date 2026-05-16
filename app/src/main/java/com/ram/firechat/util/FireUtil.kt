package com.ram.firechat.util

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.ram.firechat.model.AvatarModel
import com.ram.firechat.model.Chats
import com.ram.firechat.model.MessageModel
import com.ram.firechat.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

object FireUtil {

    fun getFireAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun createUser(userName: String, password: String, navController: NavController) {
        val fireAuth = FirebaseAuth.getInstance()
        fireAuth.createUserWithEmailAndPassword(userName, password).addOnSuccessListener { info ->
            addUser(info, navController)
        }.addOnFailureListener { error ->
            Log.d("ERROR", "createUser: ${error}")
        }
    }

    fun loginUser(userName: String, password: String, navController: NavController) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(userName, password).addOnSuccessListener { info ->
            checkIfUserNameExists(info, navController)
        }.addOnFailureListener { error ->
            Log.d("ERROR", "loginUser: ${error}")
        }
    }

    fun checkIfUserNameExists(info: AuthResult?, navController: NavController) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(info?.user?.uid.toString()).addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value?.get("userName").toString().isNotEmpty()) {
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                } else {
                    navController.navigate("set-username") {
                        popUpTo(0)
                    }
                }
            }
    }

    private fun addUser(info: AuthResult, navController: NavController) {

        val firestore = FirebaseFirestore.getInstance()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            firestore.collection("users")
                .document(info.user?.uid.toString())
                .set(
                    mapOf(
                        "uid" to info.user?.uid,
                        "avatarChoice" to 0,
                        "fcm_token" to task.result,
                        "userName" to ""
                    )
                ).addOnCompleteListener {
                    navController.navigate("set-username")
                }
        }
    }

    fun updateUser(navController: NavController, avatarModel: AvatarModel, userName: String) {
        val firestore = FirebaseFirestore.getInstance()
        val fAuth = getFireAuth()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            firestore.collection("users")
                .document(fAuth.uid.toString())
                .set(
                    mapOf(
                        "uid" to fAuth.uid.toString(),
                        "avatarChoice" to avatarModel.imgIndex,
                        "fcm_token" to task.result,
                        "userName" to userName
                    ), SetOptions.merge()
                ).addOnCompleteListener {
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                }
        }
    }

    fun signOut(navController: NavController) {
        FirebaseAuth.getInstance().signOut()
        navController.navigate("login")
    }

    fun getCurrentUser(): Flow<UserModel?> = callbackFlow {
        val listener = FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("uid", getFireAuth().currentUser?.uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = value?.documents?.map { item ->
                    UserModel(
                        uid = item.get("uid").toString(),
                        userName = item.get("userName").toString(),
                        avatarChoice = item.get("avatarChoice").toString().toInt(),
                        fcmToken = item.get("fcm_token").toString()
                    )
                } ?: null
                trySend(user?.get(0))
            }
        awaitClose { listener.remove() }
    }

    fun getUsers(): Flow<List<UserModel>> = callbackFlow {
        val currentUser = getFireAuth().currentUser
        val listener = FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val users = value?.documents?.map { item ->
                    UserModel(
                        uid = item.get("uid").toString(),
                        avatarChoice = item.get("avatarChoice").toString().toInt(),
                        userName = item.get("userName").toString(),
                        fcmToken = item.get("fcm_token").toString()
                    )

                } ?: emptyList()

                trySend(users.filter { it.uid != currentUser?.uid && it.userName.isNotEmpty() })
            }
        awaitClose {
            listener.remove()
        }
    }

    fun getChats(): Flow<List<Chats>> = callbackFlow {
        val currentUserId = getFireAuth().currentUser?.uid
        val listener = FirebaseFirestore.getInstance()
            .collection("chat")
            .whereArrayContains("participants", currentUserId.toString())
            .orderBy("lastMessageTimeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chats = value?.documents?.map { item ->

                    val participantsInfo =
                        item.get("participantsInfo") as Map<*, Map<String, Any>>
                    val otherUserId =
                        (item.get("participants") as List<Any>).firstOrNull { it != currentUserId }
                    val otherUserInfo = participantsInfo.get(otherUserId)

                    Chats(
                        chatId = item.id,
                        receiverUserId = otherUserId.toString(),
                        userName = otherUserInfo?.get("userName").toString(),
                        avatarChoice = otherUserInfo?.get("avatarChoice").toString().toInt(),
                        lastMessage = item.get("lastMessage").toString(),
                    )
                } ?: emptyList()

                trySend(chats)
            }

        awaitClose { listener.remove() }
    }

    fun getChatMessages(chatId: String): Flow<List<MessageModel>> = callbackFlow {
        val listener = FirebaseFirestore.getInstance().collection("chat")
            .document(chatId).collection("messages").orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = value?.documents?.map { item ->
                    val timeStamp = item.getTimestamp("timeStamp")


                    val formatedTime = timeStamp?.toDate()?.let { date ->
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                    } ?: ""
                    val timeNow = LocalTime.now()
//                    getDate(timeStamp)
//                val formattedDate = timeStamp?.toDate()?.let { date ->
//                    SimpleDateFormat("MMM - dd", Locale.getDefault()).format(date)
//                } ?: ""

                    val msgTime = timeStamp?.toDate() ?: java.util.Date()
                    MessageModel(
                        senderId = item.get("senderId").toString(),
                        text = item.get("msgText").toString(),
                        timeStamp = formatedTime,
                        dateStamp = getDate(timeStamp),
                        time = msgTime
                    )
                } ?: emptyList()

                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    private fun getDate(timeStamp: Timestamp?): String {

        if (timeStamp == null) {
            return "Today"
        }
        val msgDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timeStamp?.toDate())
        val today = LocalDate.now()
        val yesterDay = today.minusDays(1)
        return when {
            msgDate == today.toString() -> "Today"
            msgDate == yesterDay.toString() -> "Yesterday"
            else -> SimpleDateFormat("MMM - dd", Locale.getDefault()).format(timeStamp?.toDate()).toString()
        }
    }

    fun sendMsg(context: Context, text: String, userModels: List<UserModel?>) {
        if (userModels.isEmpty()) return
        val senderUserId = userModels[0]?.uid.toString()
        val sender = userModels[0]?.userName ?: "Unknown"
        val senderAvatar = userModels[0]?.avatarChoice
        val receiverUserId = userModels[1]?.uid.toString()
        val receiver = userModels[1]?.userName ?: "Unknown"
        val receiverAvatar = userModels[1]?.avatarChoice
        val chatId = listOf(senderUserId, receiverUserId).sorted().joinToString("_")

        FirebaseFirestore.getInstance()
            .collection("chat")
            .document(chatId)
            .collection("messages").add(
                mapOf(
                    "senderId" to senderUserId,
                    "senderName" to sender,
                    "msgText" to text,
                    "timeStamp" to FieldValue.serverTimestamp()
                )
            ).addOnSuccessListener {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val token = fetchUserFCMToken(receiverUserId)
//
//                    sendFCMNotification(context, text, userModels[1], token, chatId)
//                }

                FirebaseFirestore.getInstance()
                    .collection("chat")
                    .document(chatId)
                    .set(
                        mapOf(
                            "participants" to arrayOf(senderUserId, receiverUserId).sorted(),
                            "participantsInfo" to mapOf(
                                senderUserId to mapOf(
                                    "userName" to sender,
                                    "avatarChoice" to senderAvatar
                                ),
                                receiverUserId to mapOf(
                                    "userName" to receiver,
                                    "avatarChoice" to receiverAvatar
                                )
                            ),
                            "lastMessage" to text,
                            "lastSenderId" to senderUserId,
                            "lastMessageTimeStamp" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    )
            }
    }

    private fun sendFCMNotification(
        context: Context,
        text: String,
        userModel: UserModel?,
        token: String?,
        chatId: String
    ) {

        if (token.isNullOrEmpty()) {
            Log.d("FFFFFTOLKEN", "sendFCMNotification: ${token}")
            return
        }
        if (userModel == null) return

        val accessToken = getAccessToken(context)

        //dyV_MrqJTFeRKuR2l2s80G:APA91bHeWCKUCBpm5qtJOO_ArYkDv_IRgSnqOn0nwP9XfR_bEtaAbVpsRwamhJcY43cB724cNEuR5EwgDQxJM0RUauijLcuRCZW_SLlum7xh710HoxEmaEw
        val payloadJson = """
            {
                "message":  {
                    "token": "${token}",
                    "data": {
                        "title": "New Message",
                        "body": "${text}",
                        "chatId": "${chatId}",
                        "senderName": "${userModel.userName}"
                    }
                }
            }
        """.trimIndent()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/v1/projects/firechat-bb308/messages:send")
            .addHeader("Authorization", "Bearer ${accessToken}")
            .addHeader("Content-Type", "application/json")
            .post(payloadJson.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()

        response.use {
            if (it.isSuccessful) {
                Log.d("FFFFFTOLKEN", "sendFCMNotification: executed successfully")
            }
        }
    }

    fun getAccessToken(context: Context): String {
        val inputStream = context.assets.open("firechat_cloud_service.json")

        val creds = GoogleCredentials.fromStream(
            inputStream
        ).createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

        creds.refreshIfExpired()
        Log.d("IMPTOKEN", "getAccessToken: ${creds.accessToken.tokenValue}")
        return creds.accessToken.tokenValue
    }

    suspend fun fetchUserFCMToken(receiverId: String): String? {
        return FirebaseFirestore.getInstance()
            .collection("users")
            .document(receiverId)
            .get()
            .await()
            .getString("fcm_token")
    }


}
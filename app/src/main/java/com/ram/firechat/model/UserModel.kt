package com.ram.firechat.model

data class UserModel(
    val uid: String,
    val avatarChoice: Int,
    val userName: String,
    val fcmToken: String
)

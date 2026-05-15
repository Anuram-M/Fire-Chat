package com.ram.firechat.model

data class Chats(
    val chatId: String,
    val receiverUserId: String,
    val userName: String,
    val avatarChoice: Int,
    val lastMessage: String,
)

package com.ram.firechat.model

data class MessageModel(
    val senderId: String,
    val text: String,
    val time: java.util.Date,
    val timeStamp: String,
    val dateStamp: String
)

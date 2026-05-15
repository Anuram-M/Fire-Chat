package com.ram.firechat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ram.firechat.model.UserModel
import com.ram.firechat.util.FireUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class FireViewModel: ViewModel() {
    val _otherUser = MutableStateFlow<UserModel?>(null)
    val otherUser = _otherUser.asStateFlow()

    val _todaysQuote = MutableStateFlow<String?>(null)
    val quoteOfTheDay = _todaysQuote.asStateFlow()



    val _chatId = MutableStateFlow<String?>(null)


    val users = FireUtil.getUsers().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<UserModel>()
    )

    val chats = FireUtil.getChats().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val currentUser = FireUtil.getCurrentUser().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setChatId(chatId: String) {
        _chatId.value = chatId
    }

    val chatMessages = _chatId
        .filterNotNull()
        .flatMapLatest { id ->
            FireUtil.getChatMessages(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateChatItems(userModel: UserModel) {
        _otherUser.value = userModel
    }

    fun setTodaysQuote(quote: String) {
        _todaysQuote.value = quote
    }

}
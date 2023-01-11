// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.ui.chat.ChatAdapter
import com.azure.android.communication.ui.chat.ChatAdapterBuilder
import com.example.chat.model.ChatThreadInfoViewModel
import com.example.chat.model.Tabs
import com.example.chat.model.User
import com.example.chat.services.AcsInfoService
import com.example.chat.services.AuthService
import com.example.chat.services.AuthServiceImpl
import com.example.chat.services.ChatService
import com.example.chat.services.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MainViewModel : ViewModel() {

    val openedThread = MutableStateFlow<ChatThreadInfoViewModel?>(null)
    private val userService = UserService()
    private val authService: AuthService = AuthServiceImpl()

    private val displayLogInMutableFlow = MutableStateFlow(true)
    private val displayRegisterMutableFlow = MutableStateFlow(true)
    private val displayChatsMutableFlow = MutableStateFlow(false)
    private val displayContactsMutableFlow = MutableStateFlow(false)

    private val canStartThreadMutableFlow = MutableStateFlow(false)
    private val selectedContacts = mutableListOf<User>()
    private val chatThreadsMutableFlow = MutableStateFlow(listOf<ChatThreadInfoViewModel>())

    private lateinit var acsEndpoint: String
    private lateinit var chatService: ChatService

    private var chatAdapterMap = mutableMapOf<String, ChatAdapter>()

    val displayLogIn: StateFlow<Boolean> = displayLogInMutableFlow
    val displayRegister: StateFlow<Boolean> = displayRegisterMutableFlow
    val displayChats: StateFlow<Boolean> = displayChatsMutableFlow
    val displayContacts: StateFlow<Boolean> = displayContactsMutableFlow
    val canStartThread: StateFlow<Boolean> = canStartThreadMutableFlow
    val chatThreads: StateFlow<List<ChatThreadInfoViewModel>> = chatThreadsMutableFlow

    var currentUser: User? = null
    var selectedTab = MutableStateFlow(Tabs.LogIn)

    suspend fun init() {
        acsEndpoint = AcsInfoService().getAcsInfo().endpoint
    }

    suspend fun getUsers(): List<User> {
        return userService.getUsers()
    }

    suspend fun logIn(context: Context) {
        val currentUser = currentUser ?: throw IllegalStateException("user is not selected")
        val token = authService.getToken(currentUser.id)
        currentUser.token = token

        displayLogInMutableFlow.value = false
        displayRegisterMutableFlow.value = false
        displayChatsMutableFlow.value = true
        displayContactsMutableFlow.value = true

        chatService = ChatService(acsEndpoint, token)
        loadChatsThreads()
        startNotifications(context)
    }

    private fun startNotifications(context: Context) {
        chatService.startRealtimeNotifications(context)

        chatService.addOnChatThreadCreated {
            CoroutineScope(Dispatchers.Main).launch {
                this@MainViewModel.loadChatsThreads()
            }
        }
    }

    suspend fun register(context: Context, name: String) {
        currentUser = userService.createUser(name)

        displayLogInMutableFlow.value = false
        displayRegisterMutableFlow.value = false
        displayChatsMutableFlow.value = true
        displayContactsMutableFlow.value = true

        logIn(context)
    }

    fun addSelectedContact(user: User) {
        selectedContacts.add(user)
        canStartThreadMutableFlow.value = true
    }

    fun removeSelectedContact(user: User) {
        selectedContacts.remove(user)
        canStartThreadMutableFlow.value = selectedContacts.any()
    }

    suspend fun createChatThread() {
        if (selectedContacts.isEmpty()) throw IllegalStateException("no contacts selected to start chat")

        var chatThreadId: String
        withContext(Dispatchers.IO) {
            chatThreadId = chatService.createChatThread(selectedContacts + listOf(currentUser!!)).id
        }
        loadChatsThreads()
        openedThread.value = chatThreadsMutableFlow.value.find { it.id == chatThreadId }
    }

    private suspend fun loadChatsThreads() {
        chatThreadsMutableFlow.value = withContext(Dispatchers.IO) {
            chatService.getChatThreads().map {
                ChatThreadInfoViewModel(it.id, it.topic)
            }
        }
    }

    fun getChatAdapter(context: Context, threadId: String): ChatAdapter {
        return this.chatAdapterMap[threadId] ?: createChatAdapter(context, threadId)
    }

    private fun createChatAdapter(context: Context, threadId: String): ChatAdapter {
            val chatAdapter = ChatAdapterBuilder()
                .identity(currentUser!!.communicationIdentifier)
                .endpoint(acsEndpoint)
                .displayName(currentUser!!.name)
                .credential(CommunicationTokenCredential(currentUser!!.token))
                .threadId(threadId)
                .build()
            chatAdapter.connect(context)
            this.chatAdapterMap[threadId] = chatAdapter
            return chatAdapter
    }
}

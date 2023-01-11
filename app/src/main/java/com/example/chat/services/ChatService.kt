// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat.services

import android.content.Context
import android.util.Log
import com.azure.android.communication.chat.ChatClient
import com.azure.android.communication.chat.ChatClientBuilder
import com.azure.android.communication.chat.models.ChatEventType
import com.azure.android.communication.chat.models.ChatParticipant
import com.azure.android.communication.chat.models.ChatThreadItem
import com.azure.android.communication.chat.models.ChatThreadProperties
import com.azure.android.communication.chat.models.CreateChatThreadOptions
import com.azure.android.communication.chat.models.CreateChatThreadResult
import com.azure.android.communication.common.CommunicationTokenCredential
import com.example.chat.model.User

internal class ChatService(endpoint: String, userToken: String) {

    private val chatClient: ChatClient by lazy {
        ChatClientBuilder()
            .endpoint(endpoint)
            .credential(CommunicationTokenCredential(userToken))
            .buildClient()
    }

    fun addOnChatThreadCreated(callback: (threadId: String) -> Unit) {
        try {
            chatClient.addEventHandler(ChatEventType.CHAT_THREAD_CREATED) {
                callback(it.chatThreadId!!)
            }
        } catch (ex: Exception) {
            Log.d("chat demo", "addOnChatThreadCreate", ex)
        }
    }

    fun startRealtimeNotifications(context: Context) {
        chatClient.startRealtimeNotifications(context) { }
    }

    fun getChatThreads(): List<ChatThreadItem> {

        val chatThreadsPaged = chatClient.listChatThreads()

        val chatThreads = mutableListOf<ChatThreadItem>()

        chatThreadsPaged.byPage().forEach { page ->
            chatThreads.addAll(page.elements)
        }
        return chatThreads
    }

    fun createChatThread(users: List<User>): ChatThreadProperties {

        val participants = users.map { user ->
            ChatParticipant()
                .setCommunicationIdentifier(user.communicationIdentifier)
                .setDisplayName(user.name)
        }

        val topic = users.joinToString(", ")
        val repeatabilityRequestID = ""

        val createChatThreadOptions: CreateChatThreadOptions = CreateChatThreadOptions()
            .setTopic(topic)
            .setParticipants(participants)
            .setIdempotencyToken(repeatabilityRequestID)

        val createChatThreadResult: CreateChatThreadResult = chatClient.createChatThread(createChatThreadOptions)
        return createChatThreadResult.chatThreadProperties
    }
}

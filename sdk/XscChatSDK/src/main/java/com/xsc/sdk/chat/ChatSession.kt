package com.xsc.sdk.chat

import kotlinx.coroutines.flow.StateFlow

/**
 * Represents a single chat session or room.
 */
interface ChatSession {
    val sessionId: String
    val messages: StateFlow<List<ChatMessage>>

    /**
     * Sends a text message to this session.
     */
    suspend fun sendMessage(text: String)

    /**
     * Closes the session and cleans up resources.
     */
    fun disconnect()
}

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)

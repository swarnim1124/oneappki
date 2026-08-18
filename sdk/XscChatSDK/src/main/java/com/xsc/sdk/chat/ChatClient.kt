package com.xsc.sdk.chat

/**
 * An agnostic interface for a Chat Client.
 * This abstracts away whether the underlying implementation uses Firebase, WebSockets, or a 3rd party SDK.
 */
interface ChatClient {

    /**
     * Connects to the chat server using the provided authentication token.
     */
    suspend fun connect(authToken: String)

    /**
     * Disconnects from the chat server.
     */
    fun disconnect()

    /**
     * Joins or creates a session for the given room/channel ID.
     */
    suspend fun joinSession(sessionId: String): ChatSession
}

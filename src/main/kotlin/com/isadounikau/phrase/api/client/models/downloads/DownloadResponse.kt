package com.isadounikau.phrase.api.client.models.downloads

import java.nio.charset.Charset

sealed class DownloadResponse(
    open val response: Any
)

data class MessagesResponse(
    override val response: Messages
) : DownloadResponse(response)

data class ByteArrayResponse(
    override val response: ByteArray,
    val charset: Charset
) : DownloadResponse(response) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayResponse

        if (!response.contentEquals(other.response)) return false
        if (charset != other.charset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = response.contentHashCode()
        result = 31 * result + charset.hashCode()
        return result
    }

}

typealias Messages = Map<String, Message>

data class Message(
    val message: String?,
    val description: String? = null
)

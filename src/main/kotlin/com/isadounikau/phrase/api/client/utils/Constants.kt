package com.isadounikau.phrase.api.client.utils

import com.google.common.net.MediaType

object Constants {
    const val HS_OK = 200
    const val HS_BAD_REQUEST = 400
    const val HS_NO_CONTENT = 204
    const val HS_NOT_MODIFIED = 304

    const val PHRASE_API_URL = "https://api.phraseapp.com"
    val MEDIA_TYPE_JSON = MediaType.create("application", "json")!!
}

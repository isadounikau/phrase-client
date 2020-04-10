package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.utils.Constants
import java.time.Duration

data class PhraseApiClientConfig @JvmOverloads constructor(
    val authKey: String,
    val url: String = Constants.PHRASE_API_URL,
    val cleanUpFareRate: Duration = Duration.ofHours(1),
    val responseCacheExpireAfterWrite: Duration = Duration.ofDays(1),
    val eTagCacheExpireAfterWrite: Duration = Duration.ofDays(1)
)

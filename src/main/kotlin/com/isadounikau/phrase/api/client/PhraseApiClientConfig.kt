package com.isadounikau.phrase.api.client

import java.time.Duration

data class PhraseApiClientConfig(
    val url: String = "https://api.phraseapp.com",
    val authKey: String,
    val cleanUpFareRate: Duration = Duration.ofHours(1),
    val responseCacheExpireAfterWrite: Duration = Duration.ofDays(1),
    val eTagCacheExpireAfterWrite: Duration = Duration.ofDays(1)
) {
    constructor(authKey: String) : this("https://api.phraseapp.com", authKey, Duration.ofHours(1), Duration.ofDays(1), Duration.ofDays(1))
    constructor(url: String, authKey: String) : this(url, authKey, Duration.ofHours(1), Duration.ofDays(1), Duration.ofDays(1))
}

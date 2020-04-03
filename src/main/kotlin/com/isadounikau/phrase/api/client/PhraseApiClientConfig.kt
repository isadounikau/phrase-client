package com.isadounikau.phrase.api.client

data class PhraseApiClientConfig(
    val url: String = "https://api.phraseapp.com",
    val authKey: String,
    val cleanUpFareRateMilliseconds: Long = 3600000, //one hour
    val responseCacheExpireAfterWriteMilliseconds: Long = 86400000, //one day
    val eTagCacheExpireAfterWriteMilliseconds: Long = 86400000 //one day
)

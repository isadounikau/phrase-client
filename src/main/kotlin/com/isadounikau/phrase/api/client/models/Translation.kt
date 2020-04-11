package com.isadounikau.phrase.api.client.models

data class Translation(
    val id: String,
    val content: String,
    val locale: PhraseLocale,
    val key: TranslationKey
)

data class TranslationKey(
    val id: String,
    val name: String
)

data class CreateTranslation(
    val localeId: String,
    val keyId: String,
    val content: String,
    val branch: String? = null,
    val pluralSuffix: String? = null,
    val unverified: Boolean? = null,
    val excluded: Boolean? = null
)

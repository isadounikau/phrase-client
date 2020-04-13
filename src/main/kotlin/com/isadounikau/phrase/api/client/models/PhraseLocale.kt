package com.isadounikau.phrase.api.client.models

import java.time.Instant

data class CreatePhraseLocale(
    val name: String,
    val code: String,
    val branch: String? = null,
    val default: Boolean? = null,
    val mail: Boolean? = null,
    val rtl: Boolean? = null,
    val sourceLocaleId: String? = null,
    val unverifyNewTranslations: String? = null,
    val unverifyUpdatedTranslations: String? = null,
    val autotranslate: String? = null
)

data class DownloadPhraseLocaleProperties(
    val escapeSingleQuotes: Boolean?,
    val includeEmptyTranslations: Boolean?,
    val fallbackLocaleId: String?,
    val branch: String?
)

data class PhraseLocale(
    val id: String,
    val name: String,
    val code: String,
    val default: Boolean? = null,
    val main: Boolean? = null,
    val rtl: Boolean? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

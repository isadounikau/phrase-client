package com.isadounikau.phrase.api.client.models

import java.io.File
import java.time.Instant

data class PhraseProject(
    val id: String,
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: String? = null,
    val projectImageUrl: String? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

data class CreatePhraseProject(
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: Boolean? = null,
    val projectImage: File? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null
)

data class UpdatePhraseProject(
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: Boolean? = null,
    val projectImage: File? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null
)

package com.isadounikau.phrase.api.client.model

import java.io.File
import java.util.Date

data class PhraseProject(
    val id: String,
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: String? = null,
    val projectImageUrl: String? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)

typealias PhraseProjects = List<PhraseProject>

data class CreatePhraseProject(
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: String? = null,
    val projectImage: File? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null
)

data class UpdatePhraseProject(
    val name: String,
    val mainFormat: String? = null,
    val sharesTranslationMemory: String? = null,
    val projectImage: File? = null,
    val removeProjectImage: Boolean? = null,
    val accountId: String? = null
)

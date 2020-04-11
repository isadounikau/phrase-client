package com.isadounikau.phrase.api.client.models

import java.io.File
import java.time.Instant

data class Key(
    val id: String,
    val name: String,
    val description: String? = null,
    val tags: List<String>? = null,
    val nameHash: String? = null,
    val plural: String? = null,
    val dataType: String? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
    val namePlural: String? = null,
    val comments_count: String? = null,
    val maxCharactersAllowed: String? = null,
    val screenshotUrl: String? = null,
    val unformatted: String? = null,
    val xmlSpacePreserve: String? = null,
    val originalFile: String? = null,
    val formatValueType: String? = null,
    val creator: Creator? = null
)

data class CreateKey(
    val name: String,
    val tags: List<String>? = null,
    val description: String? = null,
    val branch: String? = null,
    val plural: Boolean? = null,
    val namePlural: String? = null,
    val dataType: String? = null,
    val maxCharactersAllowed: Number? = null,
    val screenshot: File? = null,
    val removeScreenshot: Boolean? = null,
    val unformatted: Boolean? = null,
    val xmlSpacePreserve: Boolean? = null,
    val originalFile: String? = null,
    val localizedFormatString: String? = null,
    val localizedFormatKey: String? = null
)

data class Creator(
    val id: String? = null,
    val username: String? = null,
    val name: String? = null
)

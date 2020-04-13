package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.models.CreateKey
import com.isadounikau.phrase.api.client.models.CreatePhraseLocale
import com.isadounikau.phrase.api.client.models.CreatePhraseProject
import com.isadounikau.phrase.api.client.models.CreateTranslation
import com.isadounikau.phrase.api.client.models.DownloadPhraseLocaleProperties
import com.isadounikau.phrase.api.client.models.Key
import com.isadounikau.phrase.api.client.models.PhraseLocale
import com.isadounikau.phrase.api.client.models.PhraseProject
import com.isadounikau.phrase.api.client.models.Translation
import com.isadounikau.phrase.api.client.models.UpdatePhraseProject
import com.isadounikau.phrase.api.client.models.downloads.DownloadResponse
import com.isadounikau.phrase.api.client.models.downloads.FileFormat
import com.isadounikau.phrase.api.client.models.downloads.Message

@Suppress("TooManyFunctions")
interface PhraseApiClient {

    fun projects(): List<PhraseProject>

    fun project(projectId: String): PhraseProject

    fun deleteProject(projectId: String): Boolean

    fun createProject(phraseProject: CreatePhraseProject): PhraseProject

    fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject

    fun locale(projectId: String, localeId: String, branch: String? = null): PhraseLocale

    fun locales(projectId: String, branch: String? = null): List<PhraseLocale>

    fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale?

    fun downloadLocale(projectId: String, localeId: String, fileFormat: FileFormat, properties: DownloadPhraseLocaleProperties? = null): DownloadResponse

    @Deprecated(
        message = "Old JSON parser",
        replaceWith = ReplaceWith("downloadLocale(projectId, localeId, FileFormat.JSON, properties)", "com.isadounikau.phrase.api.client.models.downloads.FileFormat"))
    fun downloadLocale(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties? = null): Map<String, Message>

    @Deprecated(
        message = "Old JSON parser",
        replaceWith = ReplaceWith("downloadLocale(projectId, localeId, FileFormat.JAVA_PROPERTY, properties)", "com.isadounikau.phrase.api.client.models.downloads.FileFormat"))
    fun downloadLocaleAsProperties(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties? = null): ByteArray

    fun deleteLocale(projectId: String, localeId: String, branch: String? = null): Boolean

    fun translations(project: PhraseProject, locale: PhraseLocale, branch: String? = null): List<Translation>

    fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation

    fun createKey(projectId: String, createKey: CreateKey): Key

    fun deleteKey(projectId: String, keyId: String, branch: String? = null): Boolean

}

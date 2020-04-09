package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.model.CreateKey
import com.isadounikau.phrase.api.client.model.CreatePhraseLocale
import com.isadounikau.phrase.api.client.model.CreatePhraseProject
import com.isadounikau.phrase.api.client.model.CreateTranslation
import com.isadounikau.phrase.api.client.model.DownloadPhraseLocaleProperties
import com.isadounikau.phrase.api.client.model.Key
import com.isadounikau.phrase.api.client.model.PhraseLocale
import com.isadounikau.phrase.api.client.model.PhraseLocaleMessages
import com.isadounikau.phrase.api.client.model.PhraseLocales
import com.isadounikau.phrase.api.client.model.PhraseProject
import com.isadounikau.phrase.api.client.model.PhraseProjects
import com.isadounikau.phrase.api.client.model.Translation
import com.isadounikau.phrase.api.client.model.Translations
import com.isadounikau.phrase.api.client.model.UpdatePhraseProject

@Suppress("TooManyFunctions")
interface PhraseApiClient {

    fun projects(): PhraseProjects?

    fun project(projectId: String): PhraseProject?

    fun deleteProject(projectId: String): Boolean

    fun createProject(phraseProject: CreatePhraseProject): PhraseProject?

    fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject?

    fun locale(projectId: String, localeId: String, branch: String? = null): PhraseLocale?

    fun locales(projectId: String, branch: String? = null): PhraseLocales?

    fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale?

    fun downloadLocale(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties? = null): PhraseLocaleMessages?

    fun downloadLocaleAsProperties(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties? = null): ByteArray?

    fun deleteLocale(projectId: String, localeId: String, branch: String? = null)

    fun translations(project: PhraseProject, locale: PhraseLocale, branch: String? = null): Translations?

    fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation?

    fun createKey(projectId: String, createKey: CreateKey): Key?

    fun deleteKey(projectId: String, keyId: String, branch: String? = null): Boolean

}

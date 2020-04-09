package com.isadounikau.phrase.api.client

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
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
import feign.Feign
import feign.Request
import feign.RequestInterceptor
import feign.Response
import feign.form.FormEncoder
import feign.gson.GsonDecoder
import feign.gson.GsonEncoder
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Collections
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

@Suppress("MaxLineLength", "TooManyFunctions", "TooGenericExceptionCaught")
class PhraseApiClientImpl : PhraseApiClient {

    private val log = LoggerFactory.getLogger(PhraseApiClientImpl::class.java.name)

    private val client: PhraseApi
    private val config: PhraseApiClientConfig
    private val responseCache: Cache<CacheKey, Any>

    // Response
    private val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    constructor(config: PhraseApiClientConfig) {
        this.config = config
        client = PhraseApiImpl(config)
        responseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(config.responseCacheExpireAfterWrite)
            .build<CacheKey, Any>()
        runCleaningTimer()
    }

    constructor(url: String, authKey: String) : this(PhraseApiClientConfig(url, authKey))

    constructor(authKey: String) : this(PhraseApiClientConfig(authKey))

    private fun runCleaningTimer() {
        Timer("responseCache", true).scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                try {
                    log.debug("CleanUp of responses cache started")
                    responseCache.cleanUp()
                    log.info("CleanUp of responses cache finished")
                } catch (ex: Exception) {
                    log.warn("Error during responses cleanup", ex)
                }
            }

        }, config.cleanUpFareRate.toMillis(), config.cleanUpFareRate.toMillis())
    }

    override fun projects(): PhraseProjects? {
        val response = client.projects()
        log.debug("Get projects")
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects")

        return processResponse(key, response)
    }

    override fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        log.debug("Get project [$projectId]")
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId")
        return processResponse(key, response)
    }

    override fun deleteProject(projectId: String): Boolean {
        log.debug("Delete project [$projectId]")
        val response = client.deleteProject(projectId)
        val key = CacheKey(Request.HttpMethod.DELETE, "/api/v2/projects/$projectId")
        processResponse<Void>(key, response)
        return response.status() == HttpStatus.SC_NO_CONTENT
    }

    override fun createProject(phraseProject: CreatePhraseProject): PhraseProject? {
        log.debug("Create project [$phraseProject]")
        val response = client.createProject(
            phraseProject.name,
            phraseProject.projectImage,
            phraseProject.mainFormat,
            phraseProject.sharesTranslationMemory,
            phraseProject.removeProjectImage,
            phraseProject.accountId
        )
        val key = CacheKey(Request.HttpMethod.POST, "/api/v2/projects")
        return processResponse(key, response)
    }

    override fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject? {
        log.debug("Update project [$phraseProject]")
        val response = client.updateProject(
            projectId,
            phraseProject.name,
            phraseProject.projectImage,
            phraseProject.mainFormat,
            phraseProject.sharesTranslationMemory,
            phraseProject.removeProjectImage,
            phraseProject.accountId
        )
        val key = CacheKey(Request.HttpMethod.PUT, "/api/v2/projects/$projectId")
        return processResponse(key, response)
    }

    override fun locale(projectId: String, localeId: String, branch: String?): PhraseLocale? {
        log.debug("Get locale [$localeId] for the [$branch] branch of project [$projectId]")
        val queryMap = buildQueryMap(mapOf("branch" to branch))
        val response = client.locale(projectId, localeId, queryMap)

        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/$localeId", queryMap)

        return processResponse(key, response)
    }

    override fun locales(projectId: String, branch: String?): PhraseLocales? {
        log.debug("Get locales for the [$branch] branch of project [$projectId]")
        val response = client.locales(projectId, branch)

        val queryMap = buildQueryMap(mapOf("branch" to branch))
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales", queryMap)

        return processResponse(key, response)
    }

    override fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale? {
        log.debug("Create locale [$locale] for project [$projectId]")
        val response = client.createLocale(
            projectId,
            locale.name,
            locale.code,
            locale.branch,
            locale.default,
            locale.mail,
            locale.rtl,
            locale.sourceLocaleId,
            locale.unverifyNewTranslations,
            locale.unverifyUpdatedTranslations,
            locale.autotranslate
        )
        val key = CacheKey(Request.HttpMethod.POST, "/api/v2/projects/$projectId/locales")

        return processResponse(key, response)
    }

    override fun downloadLocale(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties?): PhraseLocaleMessages? {
        log.debug("Download locale [$localeId] for project [$projectId]")

        val queryMap = buildQueryMap(mapOf(
            "file_format" to "json",
            "format_options%5Bescape_single_quotes%5D" to properties?.escapeSingleQuotes,
            "branch" to properties?.branch,
            "fallback_locale_id" to properties?.fallbackLocaleId,
            "include_empty_translations" to properties?.includeEmptyTranslations
        ))
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/download", queryMap)

        val response = client.downloadLocale(projectId, localeId, queryMap)
        return processResponse(key, response)
    }

    override fun downloadLocaleAsProperties(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties?): ByteArray? {
        log.debug("Download locale [$localeId] branch of project [$projectId]")

        val queryMap = buildQueryMap(mapOf(
            "file_format" to "properties",
            "format_options%5Bescape_single_quotes%5D" to properties?.escapeSingleQuotes,
            "branch" to properties?.branch,
            "fallback_locale_id" to properties?.fallbackLocaleId,
            "include_empty_translations" to properties?.includeEmptyTranslations
        ))
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/download", queryMap)

        val response = client.downloadLocale(projectId, localeId, queryMap)

        return processResponse(key, response)
    }

    override fun deleteLocale(projectId: String, localeId: String, branch: String?) {
        log.debug("Delete locale [$localeId] for [$branch] branch of project [$projectId]")
        client.deleteLocale(projectId, localeId, branch)
    }

    override fun translations(project: PhraseProject, locale: PhraseLocale, branch: String?): Translations? {
        log.debug("Get translations for locale [${locale.id}] for [$branch] branch of project [${project.id}]")
        val response = client.translations(project.id, locale.id, branch)

        val queryMap = buildQueryMap(mapOf(
            "branch" to branch
        ))
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/${project.id}/locales/${locale.id}/translations", queryMap)

        return processResponse(key, response)
    }

    override fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation? {
        log.debug("Creating the translation [${createTranslation.content}] for " +
            "locale [${createTranslation.localeId}] for " +
            "project [$projectId] for " +
            "key [${createTranslation.keyId}] for " +
            "branch [${createTranslation.branch}]")
        val response = client.createTranslation(projectId, createTranslation.localeId, createTranslation.keyId, createTranslation.content, createTranslation.branch)

        val key = CacheKey(Request.HttpMethod.POST, "/api/v2/projects/$projectId/translations")

        return processResponse(key, response)
    }

    override fun createKey(projectId: String, createKey: CreateKey): Key? {
        log.debug("Creating keys [${createKey.name}] for " +
            "[${createKey.branch}] branch of " +
            "project [$projectId]")
        val response = client.createKey(
            projectId,
            createKey.name,
            createKey.tags,
            createKey.description,
            createKey.branch,
            createKey.plural,
            createKey.namePlural,
            createKey.dataType,
            createKey.maxCharactersAllowed,
            createKey.screenshot,
            createKey.removeScreenshot,
            createKey.unformatted,
            createKey.xmlSpacePreserve,
            createKey.originalFile,
            createKey.localizedFormatString,
            createKey.localizedFormatKey
        )

        val key = CacheKey(Request.HttpMethod.POST, "/api/v2/projects/$projectId/keys")

        return processResponse(key, response)
    }

    override fun deleteKey(projectId: String, keyId: String, branch: String?): Boolean {
        log.debug("Deleting key [$keyId] for [${branch}] branch of project [$projectId]")
        val response = client.deleteKey(projectId, keyId, branch)
        return response.status() == HttpStatus.SC_NO_CONTENT
    }

    private inline fun <reified T> processResponse(key: CacheKey, response: Response): T? {
        log.debug("Response : status [${response.status()}] \n headers [${response.headers()}]")

        if (response.status() !in HttpStatus.SC_OK..HttpStatus.SC_BAD_REQUEST) {
            val message = response.body()?.asReader(StandardCharsets.UTF_8)?.readText()
            val warningMessage = key.url.plus("\n")
                .plus("Status : ${response.status()}")
                .plus("\n")
                .plus("Headers : \n ${response.headers().map { it.toString().plus("\n") }}")
                .plus("\n")
                .plus("Body : $message")
            log.warn(warningMessage)
            throw PhraseAppApiException(response.status(), message)
        }

        return if (response.status() == HttpStatus.SC_NOT_MODIFIED) {
            val cacheResponse = responseCache.getIfPresent(key) as T
            log.debug("Cached response : $cacheResponse")
            cacheResponse
        } else {

            val contentType = response.headers()
                .asSequence()
                .firstOrNull { HttpHeaders.CONTENT_TYPE.equals(it.key, true) }
                ?.value
                ?.first() ?: throw PhraseAppApiException("Content type is NULL")

            val mediaType = MediaType.parse(contentType)
            val responseObject = when (mediaType.subtype()) {
                MediaType.JSON_UTF_8.subtype() -> {
                    getObject(response)
                }
                MediaType.OCTET_STREAM.subtype() -> {
                    IOUtils.toByteArray(response.body().asInputStream()) as T
                }
                else -> {
                    throw PhraseAppApiException("Content Type $contentType is not supported")
                }
            }

            getETag(response)?.also {
                responseCache.put(key, responseObject)
                (client as CacheApi).putETag(key, it)
            }

            responseObject
        }
    }

    private inline fun <reified T> getObject(response: Response): T {
        try {
            val responseObject = gson.fromJson(response.body().asReader(StandardCharsets.UTF_8), T::class.java)
            log.debug("Response object : $responseObject")
            return responseObject
        } catch (ex: JsonSyntaxException) {
            log.warn(ex.message)
            throw PhraseAppApiException("Error during parsing response", ex)
        } catch (ex: JsonIOException) {
            log.warn(ex.message)
            throw PhraseAppApiException("Error during parsing response", ex)
        }
    }

    private fun getETag(response: Response): String? {
        val eTagHeader = response.headers()
            .entries
            .find { it.key.equals(HttpHeaders.ETAG, true) }
        return eTagHeader?.value?.first()
    }

    private fun buildQueryMap(map: Map<String, Any?>): Map<String, List<Any?>> = map.map {
        val key = it.key
        val value = if (it.value == null) {
            Collections.EMPTY_LIST
        } else {
            listOf(it.value)
        }
        key to value
    }.filter { it.second.isNotEmpty() }.toMap()

    @Suppress("TooManyFunctions")
    private class PhraseApiImpl(
        val config: PhraseApiClientConfig
    ) : PhraseApi, CacheApi {

        private var log = LoggerFactory.getLogger(PhraseApiImpl::class.java.name)

        private val target: PhraseApi
        private val eTagCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build<CacheKey, String>() // key : url, value : eTag

        init {
            target = Feign.builder()
                .requestInterceptor(getInterceptor())
                .decoder(GsonDecoder())
                .encoder(FormEncoder(GsonEncoder()))
                .target(PhraseApi::class.java, config.url)

            Timer("eTagCache", true).scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    try {
                        log.debug("CleanUp of eTags cache started")
                        eTagCache.cleanUp()
                        log.debug("CleanUp of eTags cache finished")
                    } catch (ex: Exception) {
                        log.warn("Error during eTags cleanup", ex)
                    }
                }
            }, config.cleanUpFareRate.toMillis(), config.cleanUpFareRate.toMillis())
        }

        private fun getInterceptor() = RequestInterceptor { template ->
            apply {
                val request = template.request()
                val key = CacheKey(
                    request.httpMethod(),
                    request.url().substringBefore('?'),
                    request.requestTemplate().queries()
                )
                template.header(HttpHeaders.IF_NONE_MATCH, getETag(key))
                template.header(HttpHeaders.AUTHORIZATION, "token ${config.authKey}")
            }
        }

        override fun putETag(key: CacheKey, eTag: String) {
            eTagCache.put(key, eTag)
        }

        override fun getETag(key: CacheKey): String? = eTagCache.getIfPresent(key)

        //PROJECT
        override fun projects(): Response = target.projects()

        override fun project(projectId: String): Response = target.project(projectId)

        override fun createProject(
            name: String,
            projectImage: File?,
            mainFormat: String?,
            sharesTranslationMemory: String?,
            removeProjectImage: Boolean?,
            accountId: String?
        ): Response = target.createProject(
            name = name,
            mainFormat = mainFormat,
            accountId = accountId,
            projectImage = projectImage,
            removeProjectImage = removeProjectImage,
            sharesTranslationMemory = sharesTranslationMemory
        )

        override fun updateProject(
            projectId: String,
            name: String,
            projectImage: File?,
            mainFormat: String?,
            sharesTranslationMemory: String?,
            removeProjectImage: Boolean?,
            accountId: String?
        ): Response = target.updateProject(
            projectId = projectId,
            name = name,
            mainFormat = mainFormat,
            accountId = accountId,
            projectImage = projectImage,
            removeProjectImage = removeProjectImage,
            sharesTranslationMemory = sharesTranslationMemory
        )

        override fun deleteProject(projectId: String): Response = target.deleteProject(projectId)

        //LOCALE
        override fun locales(projectId: String, branch: String?): Response = target.locales(projectId, branch)

        override fun locale(projectId: String, localeId: String, queries: Map<String, List<Any?>>): Response = target.locale(projectId, localeId, queries)

        override fun downloadLocale(
            projectId: String,
            localeId: String,
            queries: Map<String, List<Any?>>
        ): Response = target.downloadLocale(projectId, localeId, queries)

        override fun createLocale(
            projectId: String,
            name: String,
            code: String,
            branch: String?,
            default: Boolean?,
            mail: Boolean?,
            rtl: Boolean?,
            sourceLocaleId: String?,
            unverifyNewTranslations: String?,
            unverifyUpdatedTranslations: String?,
            autotranslate: String?
        ): Response = target.createLocale(projectId, name, code, branch, default, mail, rtl, sourceLocaleId, unverifyNewTranslations, unverifyUpdatedTranslations, autotranslate)

        override fun updateLocale(
            projectId: String,
            localeId: String,
            name: String,
            code: String,
            branch: String?,
            default: Boolean?,
            mail: Boolean?,
            rtl: Boolean?,
            sourceLocaleId: String?,
            unverifyNewTranslations: String?,
            unverifyUpdatedTranslations: String?,
            autotranslate: String?
        ): Response = target.updateLocale(projectId, localeId, name, code, branch, default, mail, rtl, sourceLocaleId, unverifyNewTranslations, unverifyUpdatedTranslations, autotranslate)

        override fun deleteLocale(projectId: String, localeId: String, branch: String?): Response = target.deleteLocale(projectId, localeId, branch)

        //TRANSLATION
        override fun translations(projectId: String, localeId: String, branch: String?): Response = target.translations(projectId, localeId, branch)

        override fun createTranslation(projectId: String, localeId: String, keyId: String, content: String, branch: String?): Response = target.createTranslation(projectId,
            localeId, keyId, content, branch)

        //KEYS
        override fun createKey(
            projectId: String,
            name: String,
            tags: ArrayList<String>?,
            description: String?,
            branch: String?,
            plural: Boolean?,
            namePlural: String?,
            dataType: String?,
            maxCharactersAllowed: Number?,
            screenshot: File?,
            removeScreenshot: Boolean?,
            unformatted: Boolean?,
            xmlSpacePreserve: Boolean?,
            originalFile: String?,
            localizedFormatString: String?,
            localizedFormatKey: String?
        ): Response = target.createKey(projectId, name, tags, description, branch, plural, namePlural, dataType, maxCharactersAllowed, screenshot, removeScreenshot, unformatted,
            xmlSpacePreserve, originalFile, localizedFormatString, localizedFormatKey)

        override fun createKey(projectId: String, name: String, branch: String?, tags: ArrayList<String>?): Response = target.createKey(projectId, name, branch, tags)

        override fun searchKey(projectId: String, localeId: String?, q: String?, branch: String?): Response = target.searchKey(projectId, localeId, q, branch)

        override fun deleteKey(projectId: String, keyId: String, branch: String?): Response = target.deleteKey(projectId, keyId, branch)
    }
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(httpStatus: Int, message: String?) : super("Code [$httpStatus] : $message")
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}



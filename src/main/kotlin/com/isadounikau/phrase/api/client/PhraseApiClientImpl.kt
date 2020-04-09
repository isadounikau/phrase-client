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
import mu.KotlinLogging
import java.nio.charset.StandardCharsets
import kotlin.concurrent.timer

private val log = KotlinLogging.logger {}

@Suppress("MaxLineLength", "TooManyFunctions", "TooGenericExceptionCaught")
class PhraseApiClientImpl(private val config: PhraseApiClientConfig) : PhraseApiClient, CacheApi {

    private val client: PhraseApi
    private val responseCache: Cache<CacheKey, Any> = CacheBuilder.newBuilder().expireAfterWrite(config.responseCacheExpireAfterWrite).build()
    private val eTagCache = CacheBuilder.newBuilder().expireAfterWrite(config.responseCacheExpireAfterWrite).build<CacheKey, String>()
    private val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    init {
        client = Feign.builder()
            .requestInterceptor(getInterceptor())
            .decoder(GsonDecoder())
            .encoder(FormEncoder(GsonEncoder()))
            .target(PhraseApi::class.java, config.url)

        timer(name = "eTagCache",
            daemon = true,
            initialDelay = config.cleanUpFareRate.toMillis(),
            period = config.cleanUpFareRate.toMillis()) {
            try {
                log.debug { "CleanUp of eTags cache started" }
                eTagCache.cleanUp()
                log.info { "CleanUp of eTags cache finished" }
            } catch (ex: Exception) {
                log.warn(ex) { "Error during eTags cleanup, $ex" }
            }
        }

        timer(name = "responseCache",
            daemon = true,
            initialDelay = config.cleanUpFareRate.toMillis(),
            period = config.cleanUpFareRate.toMillis()) {
            try {
                log.debug { "CleanUp of responses cache started" }
                responseCache.cleanUp()
                log.info { "CleanUp of responses cache finished" }
            } catch (ex: Exception) {
                log.warn(ex) { "Error during responses cleanup $ex" }
            }
        }
    }

    constructor(url: String, authKey: String) : this(PhraseApiClientConfig(url = url, authKey = authKey))

    constructor(authKey: String) : this(PhraseApiClientConfig(authKey = authKey))

    override fun projects(): PhraseProjects? {
        val response = client.projects()
        log.debug { "Get projects" }
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects")

        return processResponse(key, response)
    }

    override fun project(projectId: String): PhraseProject? {
        val response = client.project(projectId)
        log.debug { "Get project [$projectId]" }
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId")
        return processResponse(key, response)
    }

    override fun deleteProject(projectId: String): Boolean {
        log.debug { "Delete project [$projectId]" }
        val response = client.deleteProject(projectId)
        val key = CacheKey(Request.HttpMethod.DELETE, "/api/v2/projects/$projectId")
        processResponse<Void>(key, response)
        return response.status() == 204
    }

    override fun createProject(phraseProject: CreatePhraseProject): PhraseProject? {
        log.debug { "Create project [$phraseProject]" }
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
        log.debug { "Update project [$phraseProject]" }
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
        log.debug { "Get locale [$localeId] for the [$branch] branch of project [$projectId]" }
        val queryMap = buildQueryMap("branch" to branch)
        val response = client.locale(projectId, localeId, queryMap)

        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/$localeId", queryMap)

        return processResponse(key, response)
    }

    override fun locales(projectId: String, branch: String?): PhraseLocales? {
        log.debug { "Get locales for the [$branch] branch of project [$projectId]" }
        val response = client.locales(projectId, branch)

        val queryMap = buildQueryMap("branch" to branch)
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales", queryMap)

        return processResponse(key, response)
    }

    override fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale? {
        log.debug { "Create locale [$locale] for project [$projectId]" }
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
        log.debug { "Download locale [$localeId] for project [$projectId]" }

        val queryMap = buildQueryMap(
            "file_format" to "json",
            "format_options%5Bescape_single_quotes%5D" to properties?.escapeSingleQuotes,
            "branch" to properties?.branch,
            "fallback_locale_id" to properties?.fallbackLocaleId,
            "include_empty_translations" to properties?.includeEmptyTranslations
        )
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/download", queryMap)

        val response = client.downloadLocale(projectId, localeId, queryMap)
        return processResponse(key, response)
    }

    override fun downloadLocaleAsProperties(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties?): ByteArray? {
        log.debug { "Download locale [$localeId] branch of project [$projectId]" }

        val queryMap = buildQueryMap(
            "file_format" to "properties",
            "format_options%5Bescape_single_quotes%5D" to properties?.escapeSingleQuotes,
            "branch" to properties?.branch,
            "fallback_locale_id" to properties?.fallbackLocaleId,
            "include_empty_translations" to properties?.includeEmptyTranslations
        )
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/download", queryMap)

        val response = client.downloadLocale(projectId, localeId, queryMap)

        return processResponse(key, response)
    }

    override fun deleteLocale(projectId: String, localeId: String, branch: String?) {
        log.debug { "Delete locale [$localeId] for [$branch] branch of project [$projectId]" }
        client.deleteLocale(projectId, localeId, branch)
    }

    override fun translations(project: PhraseProject, locale: PhraseLocale, branch: String?): Translations? {
        log.debug { "Get translations for locale [${locale.id}] for [$branch] branch of project [${project.id}]" }
        val response = client.translations(project.id, locale.id, branch)

        val queryMap = buildQueryMap("branch" to branch)
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/${project.id}/locales/${locale.id}/translations", queryMap)

        return processResponse(key, response)
    }

    override fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation? {
        log.debug {
            "Creating the translation [${createTranslation.content}] for " +
                "locale [${createTranslation.localeId}] for " +
                "project [$projectId] for " +
                "key [${createTranslation.keyId}] for " +
                "branch [${createTranslation.branch}]"
        }
        val response = client.createTranslation(projectId, createTranslation.localeId, createTranslation.keyId, createTranslation.content, createTranslation.branch)

        val key = CacheKey(Request.HttpMethod.POST, "/api/v2/projects/$projectId/translations")

        return processResponse(key, response)
    }

    override fun createKey(projectId: String, createKey: CreateKey): Key? {
        log.debug {
            "Creating keys [${createKey.name}] for " +
                "[${createKey.branch}] branch of " +
                "project [$projectId]"
        }
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
        log.debug { "Deleting key [$keyId] for [${branch}] branch of project [$projectId]" }
        val response = client.deleteKey(projectId, keyId, branch)
        return response.status() == 204
    }

    private inline fun <reified T> processResponse(key: CacheKey, response: Response): T? {
        log.debug { "Response : status [${response.status()}] \n headers [${response.headers()}]" }

        if (response.status() !in 200..400) {
            val message = response.body()?.asReader(StandardCharsets.UTF_8)?.readText()
            log.warn {
                """
                |${key.url}
                |Status : ${response.status()}
                |Headers : ${response.headers().entries.joinToString("\n", "[", "]")}
                |Body : $message
                """.trimMargin()
            }
            throw PhraseAppApiException(response.status(), message)
        }

        return if (response.status() == 304) {
            val cacheResponse = responseCache.getIfPresent(key) as T
            log.debug { "Cached response : $cacheResponse" }
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
                    response.body().asInputStream().readBytes() as T
                }
                else -> {
                    throw PhraseAppApiException("Content Type $contentType is not supported")
                }
            }

            getETag(response)?.also {
                responseCache.put(key, responseObject)
                putETag(key, it)
            }

            responseObject
        }
    }

    private inline fun <reified T> getObject(response: Response): T {
        try {
            val responseObject = gson.fromJson(response.body().asReader(StandardCharsets.UTF_8), T::class.java)
            log.debug { "Response object : $responseObject" }
            return responseObject
        } catch (ex: JsonSyntaxException) {
            log.warn { ex.message }
            throw PhraseAppApiException("Error during parsing response", ex)
        } catch (ex: JsonIOException) {
            log.warn { ex.message }
            throw PhraseAppApiException("Error during parsing response", ex)
        }
    }

    private fun getETag(response: Response): String? {
        val eTagHeader = response.headers()
            .entries
            .find { it.key.equals(HttpHeaders.ETAG, true) }
        return eTagHeader?.value?.first()
    }

    private fun buildQueryMap(vararg entries: Pair<String, Any?>) =
        entries.filter { it.second != null }.associate { (k, v) -> k to listOf(v) }

    override fun putETag(key: CacheKey, eTag: String) {
        eTagCache.put(key, eTag)
    }

    override fun getETag(key: CacheKey): String? = eTagCache.getIfPresent(key)

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
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(httpStatus: Int, message: String?) : super("Code [$httpStatus] : $message")
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}



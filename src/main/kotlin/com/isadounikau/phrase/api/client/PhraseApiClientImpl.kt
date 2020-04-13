package com.isadounikau.phrase.api.client

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
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
import com.isadounikau.phrase.api.client.models.downloads.ByteArrayResponse
import com.isadounikau.phrase.api.client.models.downloads.DownloadResponse
import com.isadounikau.phrase.api.client.models.downloads.FileFormat
import com.isadounikau.phrase.api.client.models.downloads.Message
import com.isadounikau.phrase.api.client.models.downloads.MessagesResponse
import com.isadounikau.phrase.api.client.utils.Constants.HS_BAD_REQUEST
import com.isadounikau.phrase.api.client.utils.Constants.HS_NOT_MODIFIED
import com.isadounikau.phrase.api.client.utils.Constants.HS_NO_CONTENT
import com.isadounikau.phrase.api.client.utils.Constants.HS_OK
import feign.Feign
import feign.Request
import feign.RequestInterceptor
import feign.Response
import feign.jackson.JacksonEncoder
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import mu.KotlinLogging
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.concurrent.timer

private val log = KotlinLogging.logger {}

@Suppress("MaxLineLength", "TooManyFunctions", "TooGenericExceptionCaught", "UnstableApiUsage")
class PhraseApiClientImpl(private val config: PhraseApiClientConfig) : PhraseApiClient, CacheETagApi {

    constructor(authKey: String) : this(PhraseApiClientConfig(authKey = authKey))

    constructor(url: String, authKey: String) : this(PhraseApiClientConfig(url = url, authKey = authKey))

    private val client: PhraseApi
    private val responseCache: Cache<CacheKey, Any>
    private val eTagCache: Cache<CacheKey, String>
    private val mapper: Json

    init {
        client = Feign.builder()
            .requestInterceptor(getInterceptor())
            .encoder(JacksonEncoder())
            .target(PhraseApi::class.java, config.url)

        mapper = Json(JsonConfiguration.Stable)

        eTagCache = CacheBuilder.newBuilder().expireAfterWrite(config.responseCacheExpireAfterWrite).build()
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

        responseCache = CacheBuilder.newBuilder().expireAfterWrite(config.responseCacheExpireAfterWrite).build()
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

    override fun projects(): List<PhraseProject> {
        val response = client.projects()
        log.debug { "Get projects" }
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects")

        return processResponse(key, response)
    }

    override fun project(projectId: String): PhraseProject {
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
        return response.status() == HS_NO_CONTENT
    }

    override fun createProject(phraseProject: CreatePhraseProject): PhraseProject {
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

    override fun updateProject(projectId: String, phraseProject: UpdatePhraseProject): PhraseProject {
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

    override fun locale(projectId: String, localeId: String, branch: String?): PhraseLocale {
        log.debug { "Get locale [$localeId] for the [$branch] branch of project [$projectId]" }
        val queryMap = buildQueryMap("branch" to branch)
        val response = client.locale(projectId, localeId, queryMap)

        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/$localeId", queryMap)

        return processResponse(key, response)
    }

    override fun locales(projectId: String, branch: String?): List<PhraseLocale> {
        log.debug { "Get locales for the [$branch] branch of project [$projectId]" }
        val response = client.locales(projectId, branch)

        val queryMap = buildQueryMap("branch" to branch)
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales", queryMap)

        return processResponse(key, response)
    }

    override fun createLocale(projectId: String, locale: CreatePhraseLocale): PhraseLocale {
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

    override fun downloadLocale(projectId: String, localeId: String, fileFormat: FileFormat, properties: DownloadPhraseLocaleProperties?): DownloadResponse {
        log.debug { "Download locale [$localeId] for project [$projectId]" }

        val queryMap = buildQueryMap(
            "file_format" to fileFormat.apiName,
            "format_options%5Bescape_single_quotes%5D" to properties?.escapeSingleQuotes,
            "branch" to properties?.branch,
            "fallback_locale_id" to properties?.fallbackLocaleId,
            "include_empty_translations" to properties?.includeEmptyTranslations
        )
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/$projectId/locales/download", queryMap)

        val response = client.downloadLocale(projectId, localeId, queryMap)

        val charset = response.headers()
            .asSequence()
            .firstOrNull<Map.Entry<String, MutableCollection<String>>> { HttpHeaders.CONTENT_TYPE.equals(it.key, true) }
            ?.value
            ?.first()
            ?.let { MediaType.parse(it) }
            ?.charset()
            ?.or(StandardCharsets.UTF_8)
            ?: throw PhraseAppApiException("${HttpHeaders.CONTENT_TYPE} is NULL")

        return when (fileFormat) {
            FileFormat.JSON -> MessagesResponse(processResponse(key, response))
            FileFormat.JAVA_PROPERTY -> ByteArrayResponse(processResponse(key, response), charset)
            FileFormat.ANDROID_XML -> ByteArrayResponse(processResponse(key, response), charset)
            FileFormat.IOS_STRINGS -> ByteArrayResponse(processResponse(key, response), charset)
            FileFormat.CSV -> ByteArrayResponse(processResponse(key, response), charset)
        }
    }

    override fun downloadLocale(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties?): Map<String, Message> {
        return (downloadLocale(projectId, localeId, FileFormat.JSON, properties) as MessagesResponse).response
    }

    override fun downloadLocaleAsProperties(projectId: String, localeId: String, properties: DownloadPhraseLocaleProperties?): ByteArray {
        return (downloadLocale(projectId, localeId, FileFormat.JAVA_PROPERTY, properties) as ByteArrayResponse).response
    }

    override fun deleteLocale(projectId: String, localeId: String, branch: String?) {
        log.debug { "Delete locale [$localeId] for [$branch] branch of project [$projectId]" }
        client.deleteLocale(projectId, localeId, branch)
    }

    override fun translations(project: PhraseProject, locale: PhraseLocale, branch: String?): List<Translation> {
        log.debug { "Get translations for locale [${locale.id}] for [$branch] branch of project [${project.id}]" }
        val response = client.translations(project.id, locale.id, branch)

        val queryMap = buildQueryMap("branch" to branch)
        val key = CacheKey(Request.HttpMethod.GET, "/api/v2/projects/${project.id}/locales/${locale.id}/translations", queryMap)

        return processResponse(key, response)
    }

    override fun createTranslation(projectId: String, createTranslation: CreateTranslation): Translation {
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

    override fun createKey(projectId: String, createKey: CreateKey): Key {
        log.debug { "Creating keys [${createKey.name}] for [${createKey.branch}] branch of project [$projectId]" }
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
        return response.status() == HS_NO_CONTENT
    }

    override fun putETag(key: CacheKey, eTag: String) {
        eTagCache.put(key, eTag)
    }

    override fun getETag(key: CacheKey): String? = eTagCache.getIfPresent(key)

    private inline fun <reified T: Any> processResponse(key: CacheKey, response: Response): T {
        log.debug { "Response : status [${response.status()}] \n headers [${response.headers()}]" }

        if (response.status() !in HS_OK..HS_BAD_REQUEST) {
            val message = response.body()?.asReader(StandardCharsets.UTF_8)?.readText()?.trim()
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

        if (response.status() == HS_NOT_MODIFIED) {
            val cacheResponse = responseCache.getIfPresent(key) as T
            log.debug { "Cached response : $cacheResponse" }
            return cacheResponse
        } else {
            val contentType = response.headers()
                .asSequence()
                .firstOrNull<Map.Entry<String, MutableCollection<String>>> { HttpHeaders.CONTENT_TYPE.equals(it.key, true) }
                ?.value
                ?.first() ?: throw PhraseAppApiException("Content type is NULL")

            val responseBody = response.body()
            val mediaType = MediaType.parse(contentType)
            val responseObject = processResponse(mediaType, responseBody) as T
            getETag(response)?.also { eTag ->
                responseCache.put(key, responseObject)
                putETag(key, eTag)
            }
            return responseObject
        }
    }

    private inline fun <reified T: Any> processResponse(mediaType: MediaType, responseBody: Response.Body): T {
        val charset = mediaType.charset().or(StandardCharsets.UTF_8)
        return when (mediaType.withoutParameters()) {
            MediaType.JSON_UTF_8.withoutParameters() -> {
                getObject(responseBody, charset)
            }
            MediaType.CSV_UTF_8.withoutParameters(),
            MediaType.XML_UTF_8.withoutParameters(),
            MediaType.OCTET_STREAM.withoutParameters(),
            MediaType.PLAIN_TEXT_UTF_8.withoutParameters() -> {
                responseBody.asInputStream().readBytes() as T
            }
            else -> {
                throw PhraseAppApiException("Content Type $mediaType is not supported")
            }
        }
    }

    @OptIn(ImplicitReflectionSerializer::class)
    private inline fun <reified T: Any> getObject(responseBody: Response.Body, charset: Charset): T {
        try {
            val s = responseBody.asReader(charset).readText()
            val responseObject = mapper.parse<T>(s)
            log.debug { "Response object : $responseObject" }
            return responseObject as T
        } catch (ex: Exception) {
            log.warn { ex.message }
            throw PhraseAppApiException("Error during parsing response", ex)
        }
    }

    private fun getETag(response: Response): String? = response.headers()
        .entries
        .find { it.key.equals(HttpHeaders.ETAG, true) }
        ?.value?.first()

    private fun buildQueryMap(vararg entries: Pair<String, Any?>) =
        entries.filter { it.second != null }.associate { (k, v) -> k to listOf(v) }

    private fun getInterceptor() = RequestInterceptor { template ->
        val request = template.request()
        val key = CacheKey(
            request.httpMethod(),
            request.url().substringBefore('?'),
            request.requestTemplate().queries()
        )
        getETag(key)?.also {
            template.header(HttpHeaders.IF_NONE_MATCH, it)
        }
        template.header(HttpHeaders.AUTHORIZATION, "token ${config.authKey}")
    }
}

class PhraseAppApiException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(httpStatus: Int, message: String?) : super("Code [$httpStatus] : $message")
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}



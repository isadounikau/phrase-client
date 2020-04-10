package com.isadounikau.phrase.api.client

import feign.Headers
import feign.Param
import feign.QueryMap
import feign.Request
import feign.RequestLine
import feign.Response
import java.io.File

@Suppress("LongParameterList", "TooManyFunctions")
@Headers(
    "Content-Type: application/json"
)
interface PhraseApi {

    //Projects
    @RequestLine("GET /api/v2/projects")
    fun projects(): Response

    @RequestLine("GET /api/v2/projects/{projectId}")
    fun project(@Param("projectId") projectId: String): Response

    @RequestLine("POST /api/v2/projects")
    @Headers(
        "Content-Type: multipart/form-data"
    )
    fun createProject(
        @Param("name") name: String,
        @Param("project_image") projectImage: File?,
        @Param("main_format") mainFormat: String?,
        @Param("sharesTranslationMemory") sharesTranslationMemory: String?,
        @Param("remove_project_image") removeProjectImage: Boolean?,
        @Param("account_id") accountId: String?
    ): Response

    @RequestLine("PUT /api/v2/projects/{projectId}")
    @Headers(
        "Content-Type: multipart/form-data"
    )
    fun updateProject(
        @Param("projectId") projectId: String,
        @Param("name") name: String,
        @Param("project_image") projectImage: File?,
        @Param("main_format") mainFormat: String?,
        @Param("sharesTranslationMemory") sharesTranslationMemory: String?,
        @Param("remove_project_image") removeProjectImage: Boolean?,
        @Param("account_id") accountId: String?
    ): Response

    @RequestLine("DELETE /api/v2/projects/{projectId}")
    fun deleteProject(@Param("projectId") projectId: String): Response

    //Locales
    @RequestLine("GET /api/v2/projects/{projectId}/locales?branch={branch}")
    fun locales(@Param("projectId") projectId: String,
                @Param("branch") branch: String? = null): Response

    @RequestLine("POST /api/v2/projects/{projectId}/locales")
    fun createLocale(
        @Param("projectId") projectId: String,
        @Param("name")  name: String,
        @Param("code")  code: String,
        @Param("branch")  branch: String?,
        @Param("default")  default: Boolean?,
        @Param("mail")  mail: Boolean?,
        @Param("rtl")  rtl: Boolean?,
        @Param("source_locale_id")  sourceLocaleId: String?,
        @Param("unverify_new_translations")  unverifyNewTranslations: String?,
        @Param("unverify_updated_translations")  unverifyUpdatedTranslations: String?,
        @Param("autotranslate")  autotranslate: String?
    ): Response

    @RequestLine("PUT /api/v2/projects/{projectId}/locales/{localeId}")
    fun updateLocale(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String,
        @Param("name")  name: String,
        @Param("code")  code: String,
        @Param("branch")  branch: String?,
        @Param("default")  default: Boolean?,
        @Param("mail")  mail: Boolean?,
        @Param("rtl")  rtl: Boolean?,
        @Param("source_locale_id")  sourceLocaleId: String?,
        @Param("unverify_new_translations")  unverifyNewTranslations: String?,
        @Param("unverify_updated_translations")  unverifyUpdatedTranslations: String?,
        @Param("autotranslate")  autotranslate: String?
    ): Response

    @RequestLine("GET /api/v2/projects/{projectId}/locales/{localeId}?branch={branch}")
    fun locale(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String,
        @QueryMap queries: Map<String, List<Any?>>
    ): Response

    @RequestLine("DELETE /api/v2/projects/{projectId}/locales/{localeId}?branch={branch}")
    fun deleteLocale(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String,
        @Param("branch") branch: String? = null
    ): Response

    @RequestLine("GET /api/v2/projects/{projectId}/locales/{localeId}/download?file_format={fileFormat}" +
        "&format_options[escape_single_quotes]={escapeSingleQuotes}&branch={branch}" +
        "&fallback_locale_id={fallbackLocaleId}&include_empty_translations={includeEmptyTranslations}"
    )
    fun downloadLocale(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String,
        @QueryMap queries: Map<String, List<Any?>>
    ): Response

    //Translations
    @RequestLine("GET /api/v2/projects/{projectId}/locales/{localeId}/translations?branch={branch}")
    fun translations(
        @Param("projectId") projectId: String,
        @Param("localeId") localeId: String,
        @Param("branch") branch: String? = null
    ): Response

    @RequestLine("POST /api/v2/projects/{project_id}/translations")
    fun createTranslation(
        @Param("project_id") projectId: String,
        @Param("locale_id") localeId: String,
        @Param("key_id") keyId: String,
        @Param("content") content: String,
        @Param("branch") branch: String? = null
    ): Response


    //Keys
    @RequestLine("POST /api/v2/projects/{project_id}/keys")
    fun createKey(
        @Param("project_id") projectId: String,
        @Param("name") name: String,
        @Param("tags") tags: List<String>? = null,
        @Param("description") description: String? = null,
        @Param("branch") branch: String? = null,
        @Param("plural") plural: Boolean? = null,
        @Param("name_plural") namePlural: String? = null,
        @Param("data_type") dataType: String? = null,
        @Param("max_characters_allowed") maxCharactersAllowed: Number? = null,
        @Param("screenshot") screenshot: File? = null,
        @Param("remove_screenshot") removeScreenshot: Boolean? = null,
        @Param("unformatted") unformatted: Boolean? = null,
        @Param("xml_space_preserve") xmlSpacePreserve: Boolean? = null,
        @Param("original_file") originalFile: String? = null,
        @Param("localized_format_string") localizedFormatString: String? = null,
        @Param("localized_format_key") localizedFormatKey: String? = null
    ): Response

    @RequestLine("POST /api/v2/projects/{project_id}/keys")
    fun createKey(
        @Param("project_id") projectId: String,
        @Param("name") name: String,
        @Param("branch") branch: String? = null,
        @Param("tags") tags: ArrayList<String>? = null
    ): Response

    @RequestLine("POST /api/v2/projects/{project_id}/keys/search")
    fun searchKey(
        @Param("project_id") projectId: String,
        @Param("locale_id") localeId: String?,
        @Param("q") q: String? = null,
        @Param("branch") branch: String? = null
    ): Response

    @RequestLine("DELETE /api/v2/projects/{projectId}/keys/{keyId}")
    fun deleteKey(
        @Param("projectId") projectId: String,
        @Param("keyId") keyId: String,
        @Param("branch") branch: String? = null
    ): Response

}

interface CacheETagApi {

    fun putETag(key: CacheKey, eTag: String)

    fun getETag(key: CacheKey): String?

}

data class CacheKey(
    val httpMethod: Request.HttpMethod,
    val url: String,
    val queryMap: Map<String, Any?>? = null
)

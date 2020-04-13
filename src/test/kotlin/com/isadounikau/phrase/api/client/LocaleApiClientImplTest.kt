package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.models.CreatePhraseLocale
import com.isadounikau.phrase.api.client.models.PhraseLocale
import org.junit.Test
import java.time.Instant
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocaleApiClientImplTest : AbstractTest() {

    @Test
    fun `create locale when locale not exist then return created locale`() {
        //GIVEN project
        val projectId = "943e69b51641b00d6acbb638f62f4541"
       val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val createLocale = CreatePhraseLocale(
            name = "locale_name",
            code = Locale.GERMAN.toLanguageTag(),
            default = true,
            main = false,
            rtl = false
        )
        val expectedLocale = PhraseLocale(
            id = localeId,
            name = "locale_name",
            code = Locale.GERMANY.toLanguageTag(),
            default = true,
            main = false,
            rtl = false,
            createdAt = Instant.ofEpochSecond(1422438773),
            updatedAt = Instant.ofEpochSecond(1422438773)
        )

        //WHEN
        val actualProject = source.createLocale(projectId, createLocale)

        //THEN
        assertNotNull(actualProject)
        assertEquals(expectedLocale, actualProject)
    }

    @Test
    fun `get locales when projects exist then return locales`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"

        //WHEN
        val locales = source.locales(projectId)

        //THEN
        assertNotNull(locales)
        assertEquals(2, locales.size)
    }

    @Test
    fun `get locale when locale exist then return locale`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val expectedLocale = PhraseLocale(
            id = localeId,
            name = "locale_name",
            code = Locale.GERMANY.toLanguageTag(),
            default = true,
            main = false,
            rtl = false,
            createdAt = Instant.ofEpochSecond(1422438773),
            updatedAt = Instant.ofEpochSecond(1422438773)
        )

        //WHEN
        val actualLocale = source.locale(projectId, localeId)

        //THEN
        assertNotNull(actualLocale)
        assertEquals(expectedLocale.id, actualLocale.id)
        assertEquals(expectedLocale.name, actualLocale.name)
        assertEquals(expectedLocale.code, actualLocale.code)
        assertEquals(expectedLocale.default, actualLocale.default)
        assertEquals(expectedLocale.main, actualLocale.main)
        assertEquals(expectedLocale.rtl, actualLocale.rtl)
        assertEquals(expectedLocale, actualLocale)
    }

    @Test
    fun `get locale when locale exist and been already requested then return locale from cache`() {
        //GIVEN project Id
        val projectId = "cached"
        val localeId = "cached"

        //WHEN
        val localeOne = source.locale(projectId, localeId)
        val localeTwo = source.locale(projectId, localeId)

        //THEN
        assertNotNull(localeOne)
        assertNotNull(localeTwo)
        assertEquals(localeOne, localeTwo)
    }

    @Test
    fun `get locale when locale with branch exist and been already requested then return locale from cache`() {
        //GIVEN project Id
        val projectId = "cached"
        val localeId = "cached"
        val branch = "master"

        //WHEN
        val localeOne = source.locale(projectId, localeId, branch)
        val localeTwo = source.locale(projectId, localeId, branch)

        //THEN
        assertNotNull(localeOne)
        assertNotNull(localeTwo)
        assertEquals(localeOne, localeTwo)
    }

    @Test
    fun `get locale when locale not exist then throw exception`() {
        //GIVEN an api
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "NOT_FOUND"

        //WHEN
        val ex = assertFailsWith<PhraseAppApiException> { source.locale(projectId, localeId) }

        //THEN exception
        assertNotNull(ex)
        assertEquals(
            """
            |Code [404] : {
            |  "message": "Not Found",
            |  "documentation_url": "https://developers.phrase.com/api/"
            |}
            """.trimMargin(), ex.message)
    }

    @Test
    fun `delete project locale when project locale exist then return true`() {
        //GIVEN project id & locale id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"

        //WHEN
        val result = source.deleteLocale(projectId, localeId)

        //THEN
        assertNotNull(result)
        assertTrue { result }
    }
}

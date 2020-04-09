package com.isadounikau.phrase.api.client

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class LocaleApiClientImplTest : AbstractTest() {

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

        //WHEN
        val locale = source.locale(projectId, localeId)

        //THEN
        assertNotNull(locale)
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
"""Code [404] : {
  "message": "Not Found",
  "documentation_url": "https://developers.phrase.com/api/"
}""", ex.message)
    }
}

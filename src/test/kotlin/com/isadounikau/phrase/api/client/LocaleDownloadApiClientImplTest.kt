package com.isadounikau.phrase.api.client

import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test
import kotlin.test.assertNotNull

@ImplicitReflectionSerializer
class LocaleDownloadApiClientImplTest: AbstractTest() {

    @Test
    fun `get locales translations when translations exist then return translations as JSON`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"

        //WHEN
        val locales = source.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(locales)
    }

    @Test
    fun `get locales translations when translations exist then return translations as ByteArray`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"

        //WHEN
        val locales = source.downloadLocaleAsProperties(projectId, localeId)

        //THEN
        assertNotNull(locales)
    }
}

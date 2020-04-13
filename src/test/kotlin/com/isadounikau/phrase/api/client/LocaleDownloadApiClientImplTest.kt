package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.models.downloads.ByteArrayResponse
import com.isadounikau.phrase.api.client.models.downloads.FileFormat
import com.isadounikau.phrase.api.client.models.downloads.Message
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LocaleDownloadApiClientImplTest : AbstractTest() {

    @Test
    fun `get locales translations when translations exist then return translations as JSON`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val expectedLocaleMessages = mapOf(
            "boolean_key" to Message("--- true\n"),
            "empty_string_translation" to Message(""),
            "key_with_description" to Message("Check it out! This key has a description! (At least in some formats)", "This is the amazing description for this key!"),
            "key_with_line-break" to Message("This translations contains\na line-break."),
            "nested.deeply.key" to Message("Wow, this key is nested even deeper."),
            "nested.key" to Message("This key is nested inside a namespace."),
            "null_translation" to Message(null),
            "pluralized_key.one" to Message("Only one pluralization found."),
            "pluralized_key.other" to Message("Wow, you have %s pluralizations!"),
            "pluralized_key.zero" to Message("You have no pluralization."),
            "sample_collection" to Message("---\n- first item\n- second item\n- third item\n"),
            "simple_key" to Message("Just a simple key with a simple message."),
            "unverified_key" to Message("This translation is not yet verified and waits for it. (In some formats we also export this status)")
            )

        //WHEN
        val actualLocaleMessages = source.downloadLocale(projectId, localeId, FileFormat.JSON)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertNotNull(actualLocaleMessages.response)
        assertEquals(expectedLocaleMessages, actualLocaleMessages.response)
    }

    @Test
    fun `get locales translations when translations exist then return translations as JAVA PROPERTIES`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations.properties").readText()

        //WHEN
        val locales = source.downloadLocale(projectId, localeId, FileFormat.JAVA_PROPERTY) as ByteArrayResponse
        val actualData = String(locales.response)

        //THEN
        assertNotNull(locales)
        assertNotNull(actualData)
        assertEquals(fileContent, actualData)
    }

    @Test
    fun `get locales translations when translations exist then return translations as ANDROID XML`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations.xml").readText()

        //WHEN
        val locales = source.downloadLocale(projectId, localeId, FileFormat.ANDROID_XML) as ByteArrayResponse
        val actualData = String(locales.response)

        //THEN
        assertNotNull(locales)
        assertNotNull(actualData)
        assertEquals(fileContent, actualData)
    }

    @Test
    fun `get locales translations when translations exist then return translations as IOS STRINGS`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations.strings").readText()

        //WHEN
        val locales = source.downloadLocale(projectId, localeId, FileFormat.IOS_STRINGS) as ByteArrayResponse
        val actualData = String(locales.response)

        //THEN
        assertNotNull(locales)
        assertNotNull(actualData)
        assertEquals(fileContent, actualData)
    }

    @Test
    fun `get locales translations when translations exist then return translations as CSV`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations.csv").readText()

        //WHEN
        val locales = source.downloadLocale(projectId, localeId, FileFormat.CSV) as ByteArrayResponse
        val actualData = String(locales.response)

        //THEN
        assertNotNull(locales)
        assertNotNull(actualData)
        assertEquals(fileContent, actualData)
    }

    @Test
    fun `deprecated get locales translations when translations exist then return translations as JAVA PROPERTIES`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations.properties").readText()

        //WHEN
        val locales = source.downloadLocaleAsProperties(projectId, localeId)
        val actualData = String(locales)

        //THEN
        assertNotNull(locales)
        assertNotNull(actualData)
        assertEquals(fileContent, actualData)
    }

    @Test
    fun `deprecated get locales translations when translations exist then return translations as JSON`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val expectedLocaleMessages = mapOf(
            "boolean_key" to Message("--- true\n"),
            "empty_string_translation" to Message(""),
            "key_with_description" to Message("Check it out! This key has a description! (At least in some formats)", "This is the amazing description for this key!"),
            "key_with_line-break" to Message("This translations contains\na line-break."),
            "nested.deeply.key" to Message("Wow, this key is nested even deeper."),
            "nested.key" to Message("This key is nested inside a namespace."),
            "null_translation" to Message(null),
            "pluralized_key.one" to Message("Only one pluralization found."),
            "pluralized_key.other" to Message("Wow, you have %s pluralizations!"),
            "pluralized_key.zero" to Message("You have no pluralization."),
            "sample_collection" to Message("---\n- first item\n- second item\n- third item\n"),
            "simple_key" to Message("Just a simple key with a simple message."),
            "unverified_key" to Message("This translation is not yet verified and waits for it. (In some formats we also export this status)")
        )

        //WHEN
        val actualLocaleMessages = source.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
    }

}

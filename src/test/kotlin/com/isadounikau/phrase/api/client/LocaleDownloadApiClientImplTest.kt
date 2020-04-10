package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.model.Message
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
            "app.error.message" to Message("Hi ha hagut un error. Si us plau, intenta-ho de nou."),
            "companyInformation.companyName.label" to Message("Nom d'empresa"),
            "companyInformation.streetName.label" to Message("Adreça"),
            "companyInformation.streetNumber.label" to Message("Número de carrer"),
            "companyInformation.zipCode.label" to Message("Codi postal"),
            "contactInformation.emailAddress.company.h2" to Message("T'enviarem la teva documentació per e-mail a aquesta adreça."),
            "contactInformation.emailAddress.driver.h2" to Message("Necesites un e-mail per accedir a l'App.", "Applies to all countries except UK and Ireland")
        )

        //WHEN
        val actualLocaleMessages = source.downloadLocale(projectId, localeId)

        //THEN
        assertNotNull(actualLocaleMessages)
        assertEquals(expectedLocaleMessages, actualLocaleMessages)
    }

    @Test
    fun `get locales translations when translations exist then return translations as ByteArray`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val localeId = "a4ca9b45e8721d6636be8e8ba40a90b3"
        val fileContent = LocaleDownloadApiClientImplTest::class.java.getResource("/__files/locales/download/locales-translations").readText()

        //WHEN
        val locales = source.downloadLocaleAsProperties(projectId, localeId)
        val actualData = String(locales)

        //THEN
        assertNotNull(locales)
        assertEquals(fileContent, actualData)
    }
}

package com.isadounikau.phrase.api.client

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.Test
import java.util.UUID
import kotlin.test.assertNotNull

class PhraseApiClientImplTest {

    private val wireMockServer: WireMockServer = WireMockServer()
    private val source: PhraseApiClientImpl
    private val token = UUID.fromString("2ed95762-34eb-4f26-bfee-306a42649264").toString()

    init {
        wireMockServer.start()

        val config = PhraseApiClientConfig(
            url = "http://localhost:${wireMockServer.port()}",
            authKey = token
        )

        source = PhraseApiClientImpl(config)
    }

    @Test
    fun `get projects when projects exist then return project`() {
        //GIVEN an api

        //WHEN
        val projects = source.projects()

        //THEN
        assertNotNull(projects)
    }

    fun finalize() {
        wireMockServer.stop()
    }
}

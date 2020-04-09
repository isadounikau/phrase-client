package com.isadounikau.phrase.api.client

import com.github.tomakehurst.wiremock.WireMockServer
import kotlinx.serialization.ImplicitReflectionSerializer
import java.time.Duration
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@ImplicitReflectionSerializer
abstract class AbstractTest {

    private val wireMockServer: WireMockServer = WireMockServer()
    protected lateinit var source: PhraseApiClientImpl
    private val token = UUID.fromString("2ed95762-34eb-4f26-bfee-306a42649264").toString()

    @BeforeTest
    fun beforeTest() {
        wireMockServer.start()

        val config = PhraseApiClientConfig(
            url = "http://localhost:${wireMockServer.port()}",
            authKey = token,
            cleanUpFareRate = Duration.ofSeconds(1)
        )

        source = PhraseApiClientImpl(config)
    }

    @AfterTest
    fun afterTest() {
        wireMockServer.stop()
    }

}

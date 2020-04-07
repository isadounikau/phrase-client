package com.isadounikau.phrase.api.client

import com.github.tomakehurst.wiremock.WireMockServer
import org.junit.Test
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PhraseApiClientImplTest {

    private val wireMockServer: WireMockServer = WireMockServer()
    private lateinit var source: PhraseApiClientImpl
    private val token = UUID.fromString("2ed95762-34eb-4f26-bfee-306a42649264").toString()

    @BeforeTest
    fun beforeTest() {
        wireMockServer.start()

        val config = PhraseApiClientConfig(
            url = "http://localhost:${wireMockServer.port()}",
            authKey = token
        )

        source = PhraseApiClientImpl(config)
    }

    @AfterTest
    fun afterTest() {
        wireMockServer.stop()
    }

    @Test
    fun `get projects when projects exist then return projects`() {
        //GIVEN an api

        //WHEN
        val projects = source.projects()

        //THEN
        assertNotNull(projects)
        assertEquals(2, projects.size)
    }


    @Test
    fun `get project when project exist then return project`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"

        //WHEN
        val project = source.project(projectId)

        //THEN
        assertNotNull(project)
    }

    @Test
    //TODO find way to test it with cache check
    fun `get project when project exist and been already requested then return project from cache`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"

        //WHEN
        val projectOne = source.project(projectId)
        val projectTwo = source.project(projectId)

        //THEN
        assertNotNull(projectOne)
        assertNotNull(projectTwo)
    }


    @Test(expected = PhraseAppApiException::class)
    fun `get project when project not exist `() {
        //GIVEN an api
        val projectId = "NOT_FOUND"

        //WHEN
        source.project(projectId)

        //THEN exception
    }
}

package com.isadounikau.phrase.api.client

import com.isadounikau.phrase.api.client.model.PhraseProject
import com.isadounikau.phrase.api.client.model.PhraseProjects
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ProjectApiClientImplTest : AbstractTest() {

    @Test
    fun `get projects when projects exist then return projects`() {
        //GIVEN an api
        val expectedProjects = PhraseProjects()
        expectedProjects.add(PhraseProject(
            id = "0dcf034088258abe79837e6100eab4a1",
            name = "project-1-name",
            mainFormat = "strings",
            createdAt = Date(1376563519000),
            updatedAt = Date(1585917726000)
        ))
        expectedProjects.add(
            PhraseProject(
                id = "911c3e878eb2df212aebe5ec09777fbb",
                name = "project-2-name",
                mainFormat = "strings",
                createdAt = Date(1385454055000),
                updatedAt = Date(1585920130000)
            )
        )

        //WHEN
        val actualProjects = source.projects()

        //THEN
        assertNotNull(actualProjects)
        assertEquals(2, actualProjects.size)
        assertEquals(expectedProjects, actualProjects)
    }

    @Test
    fun `get project when project exist then return project`() {
        //GIVEN project Id
        val projectId = "943e69b51641b00d6acbb638f62f4541"
        val excretedProject = PhraseProject(
            id = "943e69b51641b00d6acbb638f62f4541",
            name = "name",
            sharesTranslationMemory = "false",
            projectImageUrl = "lenna.png",
            mainFormat = "",
            createdAt = Date(1531919753000),
            updatedAt = Date(1585901452000)
        )

        //WHEN
        val actualProject = source.project(projectId)

        //THEN
        assertNotNull(actualProject)
        assertEquals(excretedProject, actualProject)
    }

    @Test
    fun `get project when project exist and been already requested then return project from cache`() {
        //GIVEN project Id
        val projectId = "cached"
        val excretedProject = PhraseProject(
            id = "943e69b51641b00d6acbb638f62f4541",
            name = "name",
            sharesTranslationMemory = "false",
            projectImageUrl = "lenna.png",
            mainFormat = "",
            createdAt = Date(1531919753000),
            updatedAt = Date(1585901452000)
        )

        //WHEN
        val projectOne = source.project(projectId)
        val projectTwo = source.project(projectId)

        //THEN
        assertNotNull(projectOne)
        assertEquals(excretedProject, projectOne)
        assertNotNull(projectTwo)
        assertEquals(projectOne, projectTwo)
    }

    fun `get project when project not exist then throw exception`() {
        //GIVEN an api
        val projectId = "NOT_FOUND"

        //WHEN
        val ex = assertFailsWith<PhraseAppApiException> { source.project(projectId) }

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
}

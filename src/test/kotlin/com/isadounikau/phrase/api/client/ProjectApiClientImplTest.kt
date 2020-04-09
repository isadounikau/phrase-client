package com.isadounikau.phrase.api.client

import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ImplicitReflectionSerializer
class ProjectApiClientImplTest: AbstractTest() {

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
    fun `get project when project exist and been already requested then return project from cache`() {
        //GIVEN project Id
        val projectId = "cached"

        //WHEN
        val projectOne = source.project(projectId)
        val projectTwo = source.project(projectId)

        //THEN
        assertNotNull(projectOne)
        assertNotNull(projectTwo)
        assertEquals(projectOne, projectTwo)
    }

    @Test(expected = PhraseAppApiException::class)
    fun `get project when project not exist then throw exception`() {
        //GIVEN an api
        val projectId = "NOT_FOUND"

        //WHEN
        source.project(projectId)

        //THEN exception
    }
}

package com.xsc.oneapp.core.navigation

import com.xsc.oneapp.core.permissions.PermissionChecker
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NavigationRegistryTest {

    private fun contribution(destination: AppDestination) = object : NavigationContribution {
        override val destination: AppDestination = destination
    }

    private val timetable = AppDestination(
        backendKeys = setOf("timetable"),
        route = "timetable",
        label = "Timetable",
        requiredPermission = "timetable.timetable.view"
    )

    private val exam = AppDestination(
        backendKeys = setOf("exams", "exam"),
        route = "exam_graph",
        label = "Exams"
    )

    private fun registry() = NavigationRegistry(
        setOf(contribution(timetable), contribution(exam))
    )

    @Test
    fun `routeFor resolves a registered backend key`() {
        val registry = registry()

        assertEquals("timetable", registry.routeFor("timetable"))
    }

    @Test
    fun `routeFor resolves every alias for a destination with multiple backend keys`() {
        val registry = registry()

        assertEquals("exam_graph", registry.routeFor("exam"))
        assertEquals("exam_graph", registry.routeFor("exams"))
    }

    @Test
    fun `routeFor normalizes slashes case and whitespace the same way Routes does`() {
        val registry = registry()

        assertEquals("timetable", registry.routeFor(" /Timetable/ "))
    }

    @Test
    fun `routeFor returns null for an unregistered key`() {
        val registry = registry()

        assertNull(registry.routeFor("unknown_module"))
    }

    @Test
    fun `authorizedDestinations always includes a destination with no required permission`() {
        val registry = registry()
        val checker = mockk<PermissionChecker>()
        every { checker.hasPermission(any()) } returns false

        val authorized = registry.authorizedDestinations(checker)

        assertEquals(listOf(exam), authorized)
    }

    @Test
    fun `authorizedDestinations includes a gated destination once the permission is held`() {
        val registry = registry()
        val checker = mockk<PermissionChecker>()
        every { checker.hasPermission("timetable.timetable.view") } returns true

        val authorized = registry.authorizedDestinations(checker)

        assertEquals(setOf(timetable, exam), authorized.toSet())
    }
}

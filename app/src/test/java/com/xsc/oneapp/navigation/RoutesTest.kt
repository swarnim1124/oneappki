package com.xsc.oneapp.navigation

import com.xsc.oneapp.core.navigation.AppDestination
import com.xsc.oneapp.core.navigation.NavigationContribution
import com.xsc.oneapp.core.navigation.NavigationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    private fun contribution(destination: AppDestination) = object : NavigationContribution {
        override val destination: AppDestination = destination
    }

    /** Empty registry - every already-shipped module resolves through the primary
     * `when` and should never need it. */
    private val emptyRegistry = NavigationRegistry(emptySet())

    @Test
    fun `every module Routes has always shipped resolves without consulting the registry`() {
        assertEquals("profile_route", Routes.destinationFor("profile", emptyRegistry))
        assertEquals(Routes.EXAMS, Routes.destinationFor("exams", emptyRegistry))
        assertEquals(Routes.EXAMS, Routes.destinationFor("exam", emptyRegistry))
        assertEquals(Routes.FEES, Routes.destinationFor("fees", emptyRegistry))
        assertEquals(Routes.FEES, Routes.destinationFor("fee", emptyRegistry))
        assertEquals(Routes.ATTENDANCE, Routes.destinationFor("attendance", emptyRegistry))
        assertEquals(Routes.CURRICULUM, Routes.destinationFor("academics", emptyRegistry))
        assertEquals(Routes.CURRICULUM, Routes.destinationFor("curriculum", emptyRegistry))
        assertEquals(Routes.TIMETABLE, Routes.destinationFor("timetable", emptyRegistry))
    }

    @Test
    fun `matching is case and whitespace insensitive same as before the registry existed`() {
        assertEquals(Routes.FEES, Routes.destinationFor(" /Fees/ ", emptyRegistry))
        assertEquals(Routes.CURRICULUM, Routes.destinationFor("Academics", emptyRegistry))
    }

    @Test
    fun `an unregistered key falls back to the generic module template`() {
        assertEquals(Routes.module("chat"), Routes.destinationFor("chat", emptyRegistry))
    }

    @Test
    fun `a key not in the hardcoded when but registered via NavigationContribution resolves through the registry`() {
        val registry = NavigationRegistry(
            setOf(
                contribution(
                    AppDestination(
                        backendKeys = setOf("chat"),
                        route = "chat_graph",
                        label = "Chat"
                    )
                )
            )
        )

        assertEquals("chat_graph", Routes.destinationFor("chat", registry))
    }
}

package com.xsc.oneapp.feature.dashboard.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRepositoryImplTest {

    @Test
    fun `returns an honest empty list rather than fabricated notifications`() = runTest {
        val repository = NotificationRepositoryImpl()

        val result = repository.getNotifications()

        assertTrue(result.isEmpty())
    }
}

package com.xsc.oneapp.feature.dashboard.domain.usecase

import com.xsc.oneapp.feature.dashboard.domain.model.NotificationItem
import com.xsc.oneapp.feature.dashboard.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): List<NotificationItem> = repository.getNotifications()
}

package com.xsc.oneapp.feature.dashboard.data.repository

import com.xsc.oneapp.feature.dashboard.domain.model.NotificationItem
import com.xsc.oneapp.feature.dashboard.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * TODO(notification-contract): no dispatcher module exists for an activity/
 * notification feed yet (see NotificationRepository's doc comment and
 * docs/BACKEND_ENDPOINT_REQUIREMENTS.md). When backend defines one - the expected
 * shape is a `view` action returning a list of {id, title, message, timestamp,
 * category, isRead} rows, filtered server-side to the caller's JWT - replace the
 * body below with an [com.xsc.sdk.network.api.ApiClient] dispatch call the same way
 * [DashboardRepositoryImpl.fetchRemoteModules] calls `m_AAA/sm_rbac/accessibleModules`.
 *
 * Until then this returns an honest empty list. It must never synthesize
 * notification content - a previous version of this screen shipped four fabricated
 * rows ("Professor Smith has published the mid-term grades...") to every signed-in
 * user, which is exactly the kind of fake-success behaviour this repository exists
 * to stop.
 */
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {
    override suspend fun getNotifications(): List<NotificationItem> = emptyList()
}

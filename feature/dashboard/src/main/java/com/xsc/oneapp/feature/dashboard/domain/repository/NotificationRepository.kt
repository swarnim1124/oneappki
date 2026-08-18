package com.xsc.oneapp.feature.dashboard.domain.repository

import com.xsc.oneapp.feature.dashboard.domain.model.NotificationItem

/**
 * The Home "Actions & Feed" preview and the Notifications tab both read from this.
 *
 * There is currently no activity/notification-feed dispatcher module in the OneApp
 * backend contract (no `m_notification`, `m_activity`, or equivalent - confirmed by
 * grepping every `*Endpoint.kt` in this codebase against the live dispatcher; see
 * docs/BACKEND_ENDPOINT_REQUIREMENTS.md for the full list of endpoints this app is
 * waiting on). [NotificationRepositoryImpl] is real, wired, and ready to call the
 * endpoint the moment backend defines one - until then it honestly returns an empty
 * list rather than fabricating notification content, matching how
 * `TokenAuthenticator.performRefresh()` (sdk/XscNetworkSDK) handles the same kind of
 * gap on the refresh-token contract.
 */
interface NotificationRepository {
    suspend fun getNotifications(): List<NotificationItem>
}

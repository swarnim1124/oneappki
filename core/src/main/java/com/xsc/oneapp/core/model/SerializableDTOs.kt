package com.xsc.oneapp.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponseEnvelope<T>(
    @SerialName("status") val status: String = "SUCCESS",
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: T? = null,
    @SerialName("error_code") val errorCode: String? = null
)

@Serializable
data class UserProfileSerializableDTO(
    @SerialName("id") val id: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("roles") val roles: List<String> = emptyList(),
    @SerialName("permissions") val permissions: List<String> = emptyList()
)

@Serializable
data class DynamicNavigationItemDTO(
    @SerialName("module_id") val moduleId: String,
    @SerialName("title") val title: String,
    @SerialName("route") val route: String,
    @SerialName("icon") val iconName: String? = null,
    @SerialName("is_enabled") val isEnabled: Boolean = true,
    @SerialName("required_permission") val requiredPermission: String? = null
)

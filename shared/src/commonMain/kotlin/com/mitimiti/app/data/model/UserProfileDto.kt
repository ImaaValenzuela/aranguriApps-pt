package com.mitimiti.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val username: String = "",
    val alias: String = "",
    val cbu: String = "",
    val avatarUrl: String? = null,
)

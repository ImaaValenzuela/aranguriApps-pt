package com.mitimiti.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val alias: String = "",
    val cbu: String = "",
)

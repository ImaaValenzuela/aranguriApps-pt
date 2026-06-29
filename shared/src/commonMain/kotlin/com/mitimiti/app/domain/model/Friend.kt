package com.mitimiti.app.domain.model

data class Friend(
    val id: String,
    val name: String,
    val alias: String? = null,
    val cbu: String? = null,
)

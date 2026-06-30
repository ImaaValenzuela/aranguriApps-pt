package com.mitimiti.app.domain.model

data class ExpenseItem(
    val id: String,
    val name: String,
    val cost: Double,
    val sharedByFriendIds: List<String>,
    val paidByFriendId: String,
)

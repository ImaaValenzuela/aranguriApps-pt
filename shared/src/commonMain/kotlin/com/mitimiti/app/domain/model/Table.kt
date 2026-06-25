package com.mitimiti.app.domain.model

data class Table(
    val id: String,
    val name: String,
    val friends: List<Friend> = emptyList(),
    val expenses: List<ExpenseItem> = emptyList(),
    // Default tip is 10%
    val tipPercentage: Double = 10.0,
    // Extras like table service (cubierto)
    val fixedExtraCost: Double = 0.0,
)

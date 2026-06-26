package com.mitimiti.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TableType {
    RESTAURANT,
    HOME_MADE,
}

@Serializable
enum class SplitType {
    EQUAL,
    BY_CONSUMPTION,
}

data class Table(
    val id: String,
    val name: String,
    val type: TableType = TableType.RESTAURANT,
    val splitType: SplitType = SplitType.BY_CONSUMPTION,
    val friends: List<Friend> = emptyList(),
    val expenses: List<ExpenseItem> = emptyList(),
    // Default tip is 10%
    val tipPercentage: Double = 10.0,
    // Extras like flat table service
    val fixedExtraCost: Double = 0.0,
    // Cubierto per person
    val cubiertoPerPerson: Double = 0.0,
)

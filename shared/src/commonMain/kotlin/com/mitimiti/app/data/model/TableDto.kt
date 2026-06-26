package com.mitimiti.app.data.model

import com.mitimiti.app.domain.model.Table
import kotlinx.serialization.Serializable

@Serializable
data class TableDto(
    val id: String = "",
    val name: String = "",
    val friends: List<FriendDto> = emptyList(),
    val expenses: List<ExpenseDto> = emptyList(),
    val tipPercentage: Double = 10.0,
    val fixedExtraCost: Double = 0.0,
) {
    fun toDomain(): Table {
        return Table(
            id = id,
            name = name,
            friends = friends.map { it.toDomain() },
            expenses = expenses.map { it.toDomain() },
            tipPercentage = tipPercentage,
            fixedExtraCost = fixedExtraCost,
        )
    }

    companion object {
        fun fromDomain(table: Table): TableDto {
            return TableDto(
                id = table.id,
                name = table.name,
                friends = table.friends.map { FriendDto.fromDomain(it) },
                expenses = table.expenses.map { ExpenseDto.fromDomain(it) },
                tipPercentage = table.tipPercentage,
                fixedExtraCost = table.fixedExtraCost,
            )
        }
    }
}

package com.mitimiti.app.data.model

import com.mitimiti.app.domain.model.ExpenseItem
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseDto(
    val id: String = "",
    val name: String = "",
    val cost: Double = 0.0,
    val sharedByFriendIds: List<String> = emptyList(),
) {
    fun toDomain(): ExpenseItem {
        return ExpenseItem(
            id = id,
            name = name,
            cost = cost,
            sharedByFriendIds = sharedByFriendIds,
        )
    }

    companion object {
        fun fromDomain(expense: ExpenseItem): ExpenseDto {
            return ExpenseDto(
                id = expense.id,
                name = expense.name,
                cost = expense.cost,
                sharedByFriendIds = expense.sharedByFriendIds,
            )
        }
    }
}

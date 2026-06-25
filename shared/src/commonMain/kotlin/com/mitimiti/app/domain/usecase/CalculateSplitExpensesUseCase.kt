package com.mitimiti.app.domain.usecase

import com.mitimiti.app.domain.model.Table

data class FriendBill(
    val friendId: String,
    val friendName: String,
    val subtotal: Double,
    val tipShare: Double,
    val extraShare: Double,
    val total: Double,
)

data class TableBillSummary(
    val tableId: String,
    val subtotal: Double,
    val totalTip: Double,
    val totalExtra: Double,
    val total: Double,
    val friendBills: List<FriendBill>,
)

class CalculateSplitExpensesUseCase {
    operator fun invoke(table: Table): TableBillSummary {
        val totalFriends = table.friends.size
        if (totalFriends == 0) {
            return TableBillSummary(table.id, 0.0, 0.0, 0.0, 0.0, emptyList())
        }

        val subtotalMap = mutableMapOf<String, Double>()
        table.friends.forEach { subtotalMap[it.id] = 0.0 }

        table.expenses.forEach { expense ->
            val sharers = expense.sharedByFriendIds
            if (sharers.isNotEmpty()) {
                val splitCost = expense.cost / sharers.size
                sharers.forEach { sharerId ->
                    subtotalMap[sharerId] = (subtotalMap[sharerId] ?: 0.0) + splitCost
                }
            }
        }

        val totalSubtotal = subtotalMap.values.sum()

        val extraCostShare = table.fixedExtraCost / totalFriends
        val totalTip = totalSubtotal * (table.tipPercentage / 100.0)

        val friendBills =
            table.friends.map { friend ->
                val subtotal = subtotalMap[friend.id] ?: 0.0
                val tipShare = subtotal * (table.tipPercentage / 100.0)
                val total = subtotal + tipShare + extraCostShare

                FriendBill(
                    friendId = friend.id,
                    friendName = friend.name,
                    subtotal = subtotal,
                    tipShare = tipShare,
                    extraShare = extraCostShare,
                    total = total,
                )
            }

        val grandTotal = totalSubtotal + totalTip + table.fixedExtraCost

        return TableBillSummary(
            tableId = table.id,
            subtotal = totalSubtotal,
            totalTip = totalTip,
            totalExtra = table.fixedExtraCost,
            total = grandTotal,
            friendBills = friendBills,
        )
    }
}

package com.mitimiti.app.domain.usecase

import com.mitimiti.app.domain.model.SplitType
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.model.TableType

data class FriendBill(
    val friendId: String,
    val friendName: String,
    val subtotal: Double,
    val tipShare: Double,
    val extraShare: Double,
    val total: Double,
    val amountPaid: Double = 0.0,
    val balance: Double = 0.0,
)

data class Transaction(
    val fromFriendId: String,
    val fromFriendName: String,
    val toFriendId: String,
    val toFriendName: String,
    val amount: Double,
)

data class TableBillSummary(
    val tableId: String,
    val subtotal: Double,
    val totalTip: Double,
    val totalExtra: Double,
    val total: Double,
    val friendBills: List<FriendBill>,
    val transactions: List<Transaction> = emptyList(),
)

class CalculateSplitExpensesUseCase {
    operator fun invoke(table: Table): TableBillSummary {
        val totalFriends = table.friends.size
        if (totalFriends == 0) {
            return TableBillSummary(table.id, 0.0, 0.0, 0.0, 0.0, emptyList(), emptyList())
        }

        // 1. Calculate how much each friend PAID
        val paidMap = mutableMapOf<String, Double>()
        table.friends.forEach { paidMap[it.id] = 0.0 }

        table.expenses.forEach { expense ->
            val payerId = expense.paidByFriendId
            if (payerId.isNotEmpty() && paidMap.containsKey(payerId)) {
                paidMap[payerId] = (paidMap[payerId] ?: 0.0) + expense.cost
            }
        }

        // 2. Calculate subtotal for each friend based on split strategy
        val subtotalMap = mutableMapOf<String, Double>()
        table.friends.forEach { subtotalMap[it.id] = 0.0 }

        val totalExpensesCost = table.expenses.sumOf { it.cost }

        if (table.splitType == SplitType.EQUAL) {
            val share = totalExpensesCost / totalFriends
            table.friends.forEach { friend ->
                subtotalMap[friend.id] = share
            }
        } else {
            table.expenses.forEach { expense ->
                val sharers = expense.sharedByFriendIds
                if (sharers.isNotEmpty()) {
                    val splitCost = expense.cost / sharers.size
                    sharers.forEach { sharerId ->
                        if (subtotalMap.containsKey(sharerId)) {
                            subtotalMap[sharerId] = (subtotalMap[sharerId] ?: 0.0) + splitCost
                        }
                    }
                }
            }
        }

        val totalSubtotal = subtotalMap.values.sum()

        // 3. Tip and Extra costs (only Restaurant has tip and cubiertoPerPerson)
        val isRestaurant = table.type == TableType.RESTAURANT
        val tipPercentage = if (isRestaurant) table.tipPercentage else 0.0
        val totalTip = totalSubtotal * (tipPercentage / 100.0)

        // Flat extras like flat table service
        val flatExtraShare = table.fixedExtraCost / totalFriends
        // Per person cubierto cost
        val cubiertoPerPerson = if (isRestaurant) table.cubiertoPerPerson else 0.0
        val extraCostShare = flatExtraShare + cubiertoPerPerson
        val totalExtra = table.fixedExtraCost + (cubiertoPerPerson * totalFriends)

        // 4. Calculate individual bills
        val friendBills =
            table.friends.map { friend ->
                val subtotal = subtotalMap[friend.id] ?: 0.0
                val tipShare = subtotal * (tipPercentage / 100.0)
                val total = subtotal + tipShare + extraCostShare
                val paid = paidMap[friend.id] ?: 0.0
                val balance = paid - total

                FriendBill(
                    friendId = friend.id,
                    friendName = friend.name,
                    subtotal = subtotal,
                    tipShare = tipShare,
                    extraShare = extraCostShare,
                    total = total,
                    amountPaid = paid,
                    balance = balance,
                )
            }

        val grandTotal = totalSubtotal + totalTip + totalExtra

        // 5. Optimize transactions to settle debts
        val transactions = calculateTransactions(friendBills)

        return TableBillSummary(
            tableId = table.id,
            subtotal = totalSubtotal,
            totalTip = totalTip,
            totalExtra = totalExtra,
            total = grandTotal,
            friendBills = friendBills,
            transactions = transactions,
        )
    }

    private fun calculateTransactions(friendBills: List<FriendBill>): List<Transaction> {
        val debtors = friendBills.filter { it.balance < -0.01 }.map { it.copy() }.toMutableList()
        val creditors = friendBills.filter { it.balance > 0.01 }.map { it.copy() }.toMutableList()

        // Sort debtors ascending (most negative first)
        debtors.sortBy { it.balance }
        // Sort creditors descending (most positive first)
        creditors.sortByDescending { it.balance }

        val transactions = mutableListOf<Transaction>()
        var debtorIdx = 0
        var creditorIdx = 0

        val debtorBalances = debtors.map { it.balance }.toMutableList()
        val creditorBalances = creditors.map { it.balance }.toMutableList()

        while (debtorIdx < debtorBalances.size && creditorIdx < creditorBalances.size) {
            val debtAmount = -debtorBalances[debtorIdx]
            val creditAmount = creditorBalances[creditorIdx]
            val transferAmount = minOf(debtAmount, creditAmount)

            if (transferAmount > 0.01) {
                transactions.add(
                    Transaction(
                        fromFriendId = debtors[debtorIdx].friendId,
                        fromFriendName = debtors[debtorIdx].friendName,
                        toFriendId = creditors[creditorIdx].friendId,
                        toFriendName = creditors[creditorIdx].friendName,
                        amount = transferAmount,
                    ),
                )
            }

            debtorBalances[debtorIdx] += transferAmount
            creditorBalances[creditorIdx] -= transferAmount

            if (debtorBalances[debtorIdx] >= -0.01) {
                debtorIdx++
            }
            if (creditorBalances[creditorIdx] <= 0.01) {
                creditorIdx++
            }
        }

        return transactions
    }
}

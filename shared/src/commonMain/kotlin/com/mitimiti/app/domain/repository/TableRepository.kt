package com.mitimiti.app.domain.repository

import com.mitimiti.app.domain.model.Table
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    suspend fun getTable(id: String): Table?

    suspend fun saveTable(table: Table)

    fun observeTable(id: String): Flow<Table?>
}

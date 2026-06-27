package com.mitimiti.app.data.repository

import com.mitimiti.app.data.datasource.FirebaseRemoteDataSource
import com.mitimiti.app.data.model.TableDto
import com.mitimiti.app.domain.model.Table
import com.mitimiti.app.domain.repository.TableRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FirebaseTableRepository(
    private val dataSource: FirebaseRemoteDataSource,
) : TableRepository {
    override suspend fun getTable(id: String): Table? {
        return dataSource.getTable(id)?.toDomain()
    }

    override suspend fun saveTable(table: Table) {
        val dto = TableDto.fromDomain(table)
        dataSource.saveTable(dto)
    }

    override fun observeTable(id: String): Flow<Table?> {
        return dataSource.observeTable(id).map { it?.toDomain() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUserTables(userId: String): Flow<List<Table>> {
        return dataSource.observeUserTableIds(userId).flatMapLatest { ids ->
            if (ids.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(ids.map { observeTable(it) }) { tablesArray ->
                    tablesArray.filterNotNull()
                }
            }
        }
    }

    override suspend fun saveUserTableRelation(
        userId: String,
        tableId: String,
    ) {
        dataSource.saveUserTableRelation(userId, tableId)
    }
}

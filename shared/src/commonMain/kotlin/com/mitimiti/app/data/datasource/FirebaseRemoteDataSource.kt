package com.mitimiti.app.data.datasource

import com.mitimiti.app.data.model.TableDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FirebaseRemoteDataSource {
    private val remoteTables = MutableStateFlow<Map<String, TableDto>>(emptyMap())

    suspend fun getTable(id: String): TableDto? {
        return remoteTables.value[id]
    }

    suspend fun saveTable(tableDto: TableDto) {
        val updated = remoteTables.value.toMutableMap()
        updated[tableDto.id] = tableDto
        remoteTables.value = updated
    }

    fun observeTable(id: String): Flow<TableDto?> {
        return remoteTables.asStateFlow().map { it[id] }
    }
}

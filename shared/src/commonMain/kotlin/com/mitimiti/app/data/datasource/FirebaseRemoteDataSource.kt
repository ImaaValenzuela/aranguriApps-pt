package com.mitimiti.app.data.datasource

import com.mitimiti.app.data.model.TableDto
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FirebaseRemoteDataSource {
    private val database = Firebase.database
    private val tablesRef = database.reference("tables")

    suspend fun getTable(id: String): TableDto? {
        return try {
            val snapshot = tablesRef.child(id).valueEvents.first()
            if (snapshot.exists) {
                snapshot.value<TableDto>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveTable(tableDto: TableDto) {
        try {
            tablesRef.child(tableDto.id).setValue(tableDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeTable(id: String): Flow<TableDto?> {
        return tablesRef.child(id).valueEvents.map { snapshot ->
            if (snapshot.exists) {
                snapshot.value<TableDto>()
            } else {
                null
            }
        }
    }

    suspend fun saveUserTableRelation(
        userId: String,
        tableId: String,
    ) {
        try {
            database.reference("user_tables").child(userId).child(tableId).setValue(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeUserTableIds(userId: String): Flow<List<String>> {
        return database.reference("user_tables").child(userId).valueEvents.map { snapshot ->
            if (snapshot.exists) {
                try {
                    val map = snapshot.value<Map<String, Boolean>>()
                    map.keys.toList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }
}

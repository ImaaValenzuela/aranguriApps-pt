package com.mitimiti.app.domain.repository

import com.mitimiti.app.domain.model.Table
import kotlinx.coroutines.flow.Flow

interface TableRepository {
    suspend fun getTable(id: String): Table?

    suspend fun saveTable(table: Table)

    fun observeTable(id: String): Flow<Table?>

    fun observeUserTables(userId: String): Flow<List<Table>>

    suspend fun saveUserTableRelation(
        userId: String,
        tableId: String,
    )

    suspend fun saveUserProfile(
        userId: String,
        profile: com.mitimiti.app.domain.model.UserProfile,
    )

    fun observeUserProfile(userId: String): Flow<com.mitimiti.app.domain.model.UserProfile?>

    suspend fun claimUsernameAndSaveProfile(
        userId: String,
        profile: com.mitimiti.app.domain.model.UserProfile,
    ): Boolean

    suspend fun getUserIdByUsername(username: String): String?

    suspend fun getUserProfile(userId: String): com.mitimiti.app.domain.model.UserProfile?

    suspend fun saveFrequentFriends(
        userId: String,
        friends: List<com.mitimiti.app.domain.model.UserProfile>,
    )

    fun observeFrequentFriends(userId: String): Flow<List<com.mitimiti.app.domain.model.UserProfile>>
}

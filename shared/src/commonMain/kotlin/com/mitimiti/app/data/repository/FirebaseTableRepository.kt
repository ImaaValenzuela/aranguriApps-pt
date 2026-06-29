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

    override suspend fun saveUserProfile(
        userId: String,
        profile: com.mitimiti.app.domain.model.UserProfile,
    ) {
        val dto =
            com.mitimiti.app.data.model.UserProfileDto(
                username = profile.username,
                alias = profile.alias,
                cbu = profile.cbu,
            )
        dataSource.saveUserProfile(userId, dto)
    }

    override fun observeUserProfile(userId: String): Flow<com.mitimiti.app.domain.model.UserProfile?> {
        return dataSource.observeUserProfile(userId).map { dto ->
            if (dto != null) {
                com.mitimiti.app.domain.model.UserProfile(
                    username = dto.username,
                    alias = dto.alias,
                    cbu = dto.cbu,
                )
            } else {
                null
            }
        }
    }

    override suspend fun claimUsernameAndSaveProfile(
        userId: String,
        profile: com.mitimiti.app.domain.model.UserProfile,
    ): Boolean {
        val dto =
            com.mitimiti.app.data.model.UserProfileDto(
                username = profile.username,
                alias = profile.alias,
                cbu = profile.cbu,
            )
        return dataSource.claimUsernameAndSaveProfile(userId, dto)
    }

    override suspend fun getUserIdByUsername(username: String): String? {
        return dataSource.getUserIdByUsername(username)
    }

    override suspend fun getUserProfile(userId: String): com.mitimiti.app.domain.model.UserProfile? {
        return dataSource.getUserProfile(userId)?.let { dto ->
            com.mitimiti.app.domain.model.UserProfile(
                username = dto.username,
                alias = dto.alias,
                cbu = dto.cbu,
            )
        }
    }

    override suspend fun saveFrequentFriends(
        userId: String,
        friends: List<com.mitimiti.app.domain.model.UserProfile>,
    ) {
        val dtos =
            friends.map {
                com.mitimiti.app.data.model.UserProfileDto(
                    username = it.username,
                    alias = it.alias,
                    cbu = it.cbu,
                )
            }
        dataSource.saveFrequentFriends(userId, dtos)
    }

    override fun observeFrequentFriends(userId: String): Flow<List<com.mitimiti.app.domain.model.UserProfile>> {
        return dataSource.observeFrequentFriends(userId).map { list ->
            list.map { dto ->
                com.mitimiti.app.domain.model.UserProfile(
                    username = dto.username,
                    alias = dto.alias,
                    cbu = dto.cbu,
                )
            }
        }
    }
}

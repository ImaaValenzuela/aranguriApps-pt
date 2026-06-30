package com.mitimiti.app.domain.repository

interface RealtimeSyncRepository {
    suspend fun startSync(tableId: String)

    suspend fun stopSync(tableId: String)
}

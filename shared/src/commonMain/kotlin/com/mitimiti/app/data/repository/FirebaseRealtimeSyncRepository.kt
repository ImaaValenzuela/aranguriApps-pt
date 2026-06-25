package com.mitimiti.app.data.repository

import com.mitimiti.app.domain.repository.RealtimeSyncRepository

class FirebaseRealtimeSyncRepository : RealtimeSyncRepository {
    override suspend fun startSync(tableId: String) {
        println("Started Firebase real-time sync for table: $tableId")
    }

    override suspend fun stopSync(tableId: String) {
        println("Stopped Firebase real-time sync for table: $tableId")
    }
}

package com.mitimiti.app.data.model

import com.mitimiti.app.domain.model.Friend
import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    val id: String = "",
    val name: String = "",
    val alias: String? = null,
    val cbu: String? = null,
) {
    fun toDomain(): Friend {
        return Friend(
            id = id,
            name = name,
            alias = alias,
            cbu = cbu,
        )
    }

    companion object {
        fun fromDomain(friend: Friend): FriendDto {
            return FriendDto(
                id = friend.id,
                name = friend.name,
                alias = friend.alias,
                cbu = friend.cbu,
            )
        }
    }
}

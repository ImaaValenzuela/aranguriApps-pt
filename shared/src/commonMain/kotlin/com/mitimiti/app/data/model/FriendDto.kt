package com.mitimiti.app.data.model

import com.mitimiti.app.domain.model.Friend

data class FriendDto(
    val id: String = "",
    val name: String = "",
) {
    fun toDomain(): Friend {
        return Friend(
            id = id,
            name = name,
        )
    }

    companion object {
        fun fromDomain(friend: Friend): FriendDto {
            return FriendDto(
                id = friend.id,
                name = friend.name,
            )
        }
    }
}

package com.mitimiti.app.presentation.perfil

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppSettings {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _alias = MutableStateFlow("mitimiti.app")
    val alias: StateFlow<String> = _alias

    private val _cbu = MutableStateFlow("0000003100012345678901")
    val cbu: StateFlow<String> = _cbu

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl

    private val _frequentFriends = MutableStateFlow<List<com.mitimiti.app.domain.model.UserProfile>>(emptyList())
    val frequentFriends: StateFlow<List<com.mitimiti.app.domain.model.UserProfile>> = _frequentFriends

    fun updateUsername(value: String) {
        _username.value = value
    }

    fun updateAlias(value: String) {
        _alias.value = value
    }

    fun updateCbu(value: String) {
        _cbu.value = value
    }

    fun updateAvatarUrl(value: String?) {
        _avatarUrl.value = value
    }


    fun addFriend(friend: com.mitimiti.app.domain.model.UserProfile) {
        if (!_frequentFriends.value.any { it.username.equals(friend.username, ignoreCase = true) }) {
            _frequentFriends.value = _frequentFriends.value + friend
        }
    }

    fun removeFriend(username: String) {
        _frequentFriends.value = _frequentFriends.value.filterNot { it.username.equals(username, ignoreCase = true) }
    }

    fun setFrequentFriends(list: List<com.mitimiti.app.domain.model.UserProfile>) {
        _frequentFriends.value = list
    }
}

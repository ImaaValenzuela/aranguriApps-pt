package com.mitimiti.app.presentation.perfil

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppSettings {
    private val _alias = MutableStateFlow("mitimiti.app")
    val alias: StateFlow<String> = _alias

    private val _cbu = MutableStateFlow("0000003100012345678901")
    val cbu: StateFlow<String> = _cbu

    private val _frequentFriends = MutableStateFlow(
        listOf("Juan", "Maria", "Carlos", "Flor", "Santi")
    )
    val frequentFriends: StateFlow<List<String>> = _frequentFriends

    fun updateAlias(value: String) {
        _alias.value = value
    }

    fun updateCbu(value: String) {
        _cbu.value = value
    }

    fun addFriend(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty() && !_frequentFriends.value.contains(trimmed)) {
            _frequentFriends.value = _frequentFriends.value + trimmed
        }
    }

    fun removeFriend(name: String) {
        _frequentFriends.value = _frequentFriends.value - name.trim()
    }

    fun setFrequentFriends(list: List<String>) {
        _frequentFriends.value = list
    }
}

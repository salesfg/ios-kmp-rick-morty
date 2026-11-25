package com.salesfg.rickmorty.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository {
    private val _favoriteIds = MutableStateFlow<MutableSet<Int>>(mutableSetOf())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    fun isFavorite(id: Int): Boolean = _favoriteIds.value.contains(id)

    fun toggle(id: Int) {
        val current = _favoriteIds.value
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _favoriteIds.value = current
    }
}

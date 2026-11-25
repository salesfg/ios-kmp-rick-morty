package com.salesfg.rickmorty.domain

import com.salesfg.rickmorty.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Guarda os personagens favoritos (em memória) independentemente da lista exibida,
 * para que eles não sumam durante uma busca.
 */
class FavoritesRepository {
    private val _favorites = MutableStateFlow<List<Character>>(emptyList())
    val favorites: StateFlow<List<Character>> = _favorites.asStateFlow()

    fun toggle(character: Character) {
        _favorites.update { current ->
            if (current.any { it.id == character.id }) {
                current.filterNot { it.id == character.id }
            } else {
                current + character
            }
        }
    }
}

package com.salesfg.rickmorty.presentation

import com.salesfg.rickmorty.BaseViewModel
import com.salesfg.rickmorty.asCFlow
import com.salesfg.rickmorty.domain.CharacterRepository
import com.salesfg.rickmorty.domain.FavoritesRepository
import com.salesfg.rickmorty.model.Character
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class CharacterViewModel(
    private val repository: CharacterRepository = CharacterRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository()
): BaseViewModel() {

    constructor() : this(CharacterRepository(), FavoritesRepository())
    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val favoriteIds = favoritesRepository.favoriteIds

    val charactersFlow = characters.asCFlow()
    val isLoadingFlow = isLoading.asCFlow()
    val favoriteIdsFlow = favoriteIds.asCFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        launch {
            _isLoading.update { true }
            try {
                val list = repository.getCharactersList()
                _characters.update { list }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            loadCharacters()
            return
        }
        GlobalScope.launch {
            _isLoading.value = true
            val result = repository.searchCharacters(query)
            _characters.value = result
            _isLoading.value = false
        }
    }

    fun toggleFavorite(id: Int) {
        favoritesRepository.toggle(id)
    }

    fun isFavorite(id: Int): Boolean = favoritesRepository.isFavorite(id)

    fun favoriteCharacters(): List<Character> {
        return _characters.value.filter { favoritesRepository.isFavorite(it.id) }
    }

    fun getCharacter(id: Int): Character? {
        return _characters.value.firstOrNull { it.id == id }
    }
}

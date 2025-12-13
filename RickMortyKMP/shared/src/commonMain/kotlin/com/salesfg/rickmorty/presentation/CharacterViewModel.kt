package com.salesfg.rickmorty.presentation

import com.salesfg.rickmorty.BaseViewModel
import com.salesfg.rickmorty.asCFlow
import com.salesfg.rickmorty.domain.CharacterRepository
import com.salesfg.rickmorty.model.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class CharacterViewModel(private val repository: CharacterRepository = CharacterRepository()
): BaseViewModel() {

    constructor() : this(CharacterRepository())
    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val charactersFlow = characters.asCFlow()
    val isLoadingFlow = isLoading.asCFlow()

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

    fun getCharacter(id: Int): Character? {
        return _characters.value.firstOrNull { it.id == id }
    }
}
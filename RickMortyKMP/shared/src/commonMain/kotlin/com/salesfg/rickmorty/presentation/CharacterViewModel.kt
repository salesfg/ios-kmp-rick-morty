package com.salesfg.rickmorty.presentation

import com.salesfg.rickmorty.BaseViewModel
import com.salesfg.rickmorty.asCFlow
import com.salesfg.rickmorty.domain.CharacterRepository
import com.salesfg.rickmorty.domain.FavoritesRepository
import com.salesfg.rickmorty.mainDispatcher
import com.salesfg.rickmorty.model.Character
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class CharacterViewModel(
    private val repository: CharacterRepository = CharacterRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(),
    dispatcher: CoroutineDispatcher = mainDispatcher,
    autoLoad: Boolean = true
): BaseViewModel(dispatcher) {

    constructor() : this(CharacterRepository(), FavoritesRepository(), mainDispatcher, true)

    /** Para previews/testes no iOS: não dispara a carga inicial. */
    constructor(autoLoad: Boolean) : this(CharacterRepository(), FavoritesRepository(), mainDispatcher, autoLoad)

    private val _characters = MutableStateFlow<List<Character>>(emptyList())
    val characters = _characters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val favorites = favoritesRepository.favorites

    val charactersFlow = characters.asCFlow()
    val isLoadingFlow = isLoading.asCFlow()
    val errorMessageFlow = errorMessage.asCFlow()
    val favoritesFlow = favorites.asCFlow()

    private var searchJob: Job? = null
    private var requestId = 0

    init {
        if (autoLoad) loadCharacters()
    }

    fun loadCharacters() {
        searchJob?.cancel()
        searchJob = launch {
            fetch { repository.getCharactersList() }
        }
    }

    /** Busca com debounce: cada chamada cancela a anterior, então só a última query chega à API. */
    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            loadCharacters()
            return
        }
        searchJob = launch {
            delay(SEARCH_DEBOUNCE_MS)
            fetch { repository.searchCharacters(query.trim()) }
        }
    }

    fun toggleFavorite(character: Character) {
        favoritesRepository.toggle(character)
    }

    fun getCharacter(id: Int): Character? {
        return _characters.value.firstOrNull { it.id == id }
            ?: favorites.value.firstOrNull { it.id == id }
    }

    private suspend fun fetch(request: suspend () -> List<Character>) {
        val id = ++requestId
        _isLoading.update { true }
        try {
            val list = request()
            _characters.update { list }
            _errorMessage.update { null }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("CharacterViewModel: falha ao carregar personagens: $e")
            // Não mantém resultados de uma query anterior como se fossem desta.
            _characters.update { emptyList() }
            _errorMessage.update { "Não foi possível carregar os personagens." }
        } finally {
            // Um request substituído não pode desligar o loading do request mais recente.
            if (id == requestId) {
                _isLoading.update { false }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}

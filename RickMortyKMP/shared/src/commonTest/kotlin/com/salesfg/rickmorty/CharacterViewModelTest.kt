package com.salesfg.rickmorty

import com.salesfg.rickmorty.domain.CharacterRepository
import com.salesfg.rickmorty.domain.FavoritesRepository
import com.salesfg.rickmorty.model.Character
import com.salesfg.rickmorty.presentation.CharacterViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterViewModelTest {
    private val rick = Character(1, "Rick Sanchez", "Alive", "Human", "rick.jpeg")
    private val morty = Character(2, "Morty Smith", "Alive", "Human", "morty.jpeg")

    private class FakeRepository : CharacterRepository() {
        val searches = mutableListOf<String>()
        var listCalls = 0
        var all: List<Character> = emptyList()
        var searchResult: suspend (String) -> List<Character> = { emptyList() }

        override suspend fun getCharactersList(): List<Character> {
            listCalls++
            return all
        }

        override suspend fun searchCharacters(name: String): List<Character> {
            searches += name
            return searchResult(name)
        }
    }

    private fun TestScope.viewModel(repository: FakeRepository) = CharacterViewModel(
        repository = repository,
        favoritesRepository = FavoritesRepository(),
        dispatcher = StandardTestDispatcher(testScheduler),
        autoLoad = false
    )

    @Test
    fun searchDebouncesAndOnlySendsLastQuery() = runTest {
        val repository = FakeRepository().apply { searchResult = { listOf(rick) } }
        val vm = viewModel(repository)

        vm.search("R")
        advanceTimeBy(100)
        vm.search("Ri")
        advanceTimeBy(100)
        vm.search("Rick")
        advanceUntilIdle()

        assertEquals(listOf("Rick"), repository.searches)
        assertEquals(listOf(rick), vm.characters.value)
        assertFalse(vm.isLoading.value)
    }

    @Test
    fun blankQueryReloadsFullList() = runTest {
        val repository = FakeRepository().apply { all = listOf(rick, morty) }
        val vm = viewModel(repository)

        vm.search("Rick")
        vm.search("  ")
        advanceUntilIdle()

        assertTrue(repository.searches.isEmpty())
        assertEquals(1, repository.listCalls)
        assertEquals(listOf(rick, morty), vm.characters.value)
    }

    @Test
    fun cancelledSearchDoesNotClearLoadingOfNewSearch() = runTest {
        val first = CompletableDeferred<List<Character>>()
        val second = CompletableDeferred<List<Character>>()
        val repository = FakeRepository().apply {
            searchResult = {
                if (it == "Rick") {
                    // Simula um engine HTTP que demora a liberar a requisição cancelada.
                    try { first.await() } finally { withContext(NonCancellable) { delay(400) } }
                } else {
                    second.await()
                }
            }
        }
        val vm = viewModel(repository)

        vm.search("Rick")
        advanceTimeBy(301)
        runCurrent()
        assertTrue(vm.isLoading.value)

        vm.search("Morty")
        advanceTimeBy(301)
        runCurrent()
        assertTrue(vm.isLoading.value)

        // O job cancelado termina o finally depois que a nova busca já ligou o loading.
        advanceTimeBy(200)
        runCurrent()
        assertTrue(vm.isLoading.value, "loading deve continuar ativo enquanto a nova busca roda")

        second.complete(listOf(morty))
        advanceUntilIdle()
        assertFalse(vm.isLoading.value)
        assertEquals(listOf(morty), vm.characters.value)
    }

    @Test
    fun failureExposesErrorAndStopsLoading() = runTest {
        val repository = FakeRepository().apply { searchResult = { error("offline") } }
        val vm = viewModel(repository)

        vm.search("Rick")
        advanceUntilIdle()

        assertNotNull(vm.errorMessage.value)
        assertFalse(vm.isLoading.value)
        assertTrue(vm.characters.value.isEmpty(), "não deve manter resultados antigos após erro")

        repository.searchResult = { listOf(rick) }
        vm.search("Rick")
        advanceUntilIdle()
        assertNull(vm.errorMessage.value)
    }

    @Test
    fun toggleFavoriteAndGetCharacterFallsBackToFavorites() = runTest {
        val repository = FakeRepository().apply {
            all = listOf(rick, morty)
            searchResult = { listOf(morty) }
        }
        val vm = viewModel(repository)
        vm.loadCharacters()
        advanceUntilIdle()

        vm.toggleFavorite(rick)
        assertEquals(listOf(rick), vm.favorites.value)

        vm.search("Morty")
        advanceUntilIdle()
        assertEquals(listOf(morty), vm.characters.value)
        assertEquals(rick, vm.getCharacter(rick.id), "favorito fora da busca ainda deve ser encontrado")

        vm.toggleFavorite(rick)
        assertTrue(vm.favorites.value.isEmpty())
    }
}

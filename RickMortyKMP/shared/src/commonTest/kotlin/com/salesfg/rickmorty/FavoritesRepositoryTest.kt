package com.salesfg.rickmorty

import com.salesfg.rickmorty.domain.FavoritesRepository
import com.salesfg.rickmorty.model.Character
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FavoritesRepositoryTest {
    private val rick = Character(1, "Rick Sanchez", "Alive", "Human", "rick.jpeg")
    private val morty = Character(2, "Morty Smith", "Alive", "Human", "morty.jpeg")

    @Test
    fun toggleAddsAndEmitsNewValue() {
        val repository = FavoritesRepository()
        val before = repository.favorites.value

        repository.toggle(rick)

        assertTrue(repository.isFavorite(rick.id))
        assertEquals(listOf(rick), repository.favorites.value)
        assertTrue(before !== repository.favorites.value, "StateFlow precisa receber uma nova instância")
    }

    @Test
    fun toggleTwiceRemoves() {
        val repository = FavoritesRepository()

        repository.toggle(rick)
        repository.toggle(morty)
        repository.toggle(rick)

        assertFalse(repository.isFavorite(rick.id))
        assertEquals(listOf(morty), repository.favorites.value)
    }
}

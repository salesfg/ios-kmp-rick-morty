package com.salesfg.rickmorty

import com.salesfg.rickmorty.domain.CharacterComparator
import com.salesfg.rickmorty.model.Character
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterComparatorTest {

    private fun character(id: Int, status: String, species: String) =
        Character(id = id, name = "Char $id", status = status, species = species, image = "")

    @Test
    fun identicalAttributesAreFullMatch() {
        val result = CharacterComparator.compare(
            character(1, "Alive", "Human"),
            character(2, "Alive", "Human")
        )
        assertEquals(2, result.matchCount)
        assertEquals(100, result.similarityPercent)
        assertTrue(result.attributes.all { it.isMatch })
    }

    @Test
    fun partialMatchIsCounted() {
        val result = CharacterComparator.compare(
            character(1, "Alive", "Human"),
            character(2, "Dead", "Human")
        )
        assertEquals(1, result.matchCount)
        assertEquals(50, result.similarityPercent)
        assertFalse(result.attributes.first { it.label == "Status" }.isMatch)
    }

    @Test
    fun comparisonIgnoresCase() {
        val result = CharacterComparator.compare(
            character(1, "unknown", "Alien"),
            character(2, "Unknown", "alien")
        )
        assertEquals(2, result.matchCount)
    }

    @Test
    fun noMatches() {
        val result = CharacterComparator.compare(
            character(1, "Alive", "Human"),
            character(2, "Dead", "Alien")
        )
        assertEquals(0, result.matchCount)
        assertEquals(0, result.similarityPercent)
    }
}

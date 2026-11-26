package com.salesfg.rickmorty.domain

import com.salesfg.rickmorty.model.Character

data class ComparisonAttribute(
    val label: String,
    val firstValue: String,
    val secondValue: String
) {
    val isMatch: Boolean
        get() = firstValue.equals(secondValue, ignoreCase = true)
}

data class CharacterComparison(
    val first: Character,
    val second: Character,
    val attributes: List<ComparisonAttribute>
) {
    val matchCount: Int
        get() = attributes.count { it.isMatch }

    val totalCount: Int
        get() = attributes.size

    /** Percentual de atributos em comum (0–100). */
    val similarityPercent: Int
        get() = if (attributes.isEmpty()) 0 else matchCount * 100 / attributes.size
}

object CharacterComparator {
    fun compare(first: Character, second: Character): CharacterComparison {
        val attributes = listOf(
            ComparisonAttribute("Status", first.status, second.status),
            ComparisonAttribute("Espécie", first.species, second.species)
        )
        return CharacterComparison(first, second, attributes)
    }
}

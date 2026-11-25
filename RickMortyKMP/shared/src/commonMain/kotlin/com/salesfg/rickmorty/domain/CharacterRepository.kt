package com.salesfg.rickmorty.domain

import com.salesfg.rickmorty.model.Character
import com.salesfg.rickmorty.network.RickMortyApi

open class CharacterRepository (
    private val api: RickMortyApi = RickMortyApi()
) {
    open suspend fun getCharactersList(): List<Character> {
        return api.getAllCharacters()
    }

    open suspend fun getCharacter(id: Int): Character {
        val list = getCharactersList()
        return list.first { it.id == id }
    }

    open suspend fun searchCharacters(name: String): List<Character> {
        return api.searchCharacters(name)
    }
}

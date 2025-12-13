package com.salesfg.rickmorty.domain

import com.salesfg.rickmorty.model.Character
import com.salesfg.rickmorty.network.RickMortyApi

class CharacterRepository (
    private val api: RickMortyApi = RickMortyApi()
) {
    suspend fun getCharactersList(): List<Character> {
        return api.getAllCharacters()
    }

    suspend fun getCharacter(id: Int): Character {
        val list = getCharactersList()
        return list.first { it.id == id }
    }
}
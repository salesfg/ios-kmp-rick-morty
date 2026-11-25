package com.salesfg.rickmorty.network

import com.salesfg.rickmorty.model.Character
import com.salesfg.rickmorty.model.CharacterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RickMortyApi {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getAllCharacters(): List<Character> {
        val response: CharacterResponse = httpClient
            .get("https://rickandmortyapi.com/api/character")
            .body()

        return response.results
    }

    suspend fun searchCharacters(name: String): List<Character> {
        val response: CharacterResponse = httpClient
            .get("https://rickandmortyapi.com/api/character/?name=$name")
            .body()

        return response.results
    }
}

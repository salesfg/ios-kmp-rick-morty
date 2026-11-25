package com.salesfg.rickmorty.network

import com.salesfg.rickmorty.model.Character
import com.salesfg.rickmorty.model.CharacterResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RickMortyApi(engine: HttpClientEngine? = null) {
    private val config: HttpClientConfig<*>.() -> Unit = {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val httpClient = if (engine != null) HttpClient(engine, config) else HttpClient(config)

    suspend fun getAllCharacters(): List<Character> {
        val response = httpClient.get(BASE_URL)
        if (!response.status.isSuccess()) throw IllegalStateException("Lista falhou: HTTP ${response.status.value}")
        return response.body<CharacterResponse>().results
    }

    // Limitação conhecida: só a primeira página (20 itens) é carregada; paginação via info.next fica para um follow-up.

    /** A API responde 404 quando nenhum personagem corresponde ao nome; tratamos como lista vazia. */
    suspend fun searchCharacters(name: String): List<Character> {
        val response = httpClient.get(BASE_URL) {
            parameter("name", name)
        }
        if (response.status == HttpStatusCode.NotFound) return emptyList()
        if (!response.status.isSuccess()) throw IllegalStateException("Busca falhou: HTTP ${response.status.value}")
        return response.body<CharacterResponse>().results
    }

    private companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/character"
    }
}

package com.salesfg.rickmorty

import com.salesfg.rickmorty.network.RickMortyApi
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RickMortyApiTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    @Test
    fun searchEncodesNameAsQueryParameter() = runTest {
        var requestedName: String? = null
        val engine = MockEngine { request ->
            requestedName = request.url.parameters["name"]
            respond(
                """{"results":[{"id":1,"name":"Rick Sanchez","status":"Alive","species":"Human","image":"x"}]}""",
                HttpStatusCode.OK,
                jsonHeaders
            )
        }

        val result = RickMortyApi(engine).searchCharacters("Rick & Morty")

        assertEquals("Rick & Morty", requestedName)
        assertEquals(1, result.size)
    }

    @Test
    fun searchReturnsEmptyListOn404() = runTest {
        val engine = MockEngine {
            respond("""{"error":"There is nothing here"}""", HttpStatusCode.NotFound, jsonHeaders)
        }

        assertTrue(RickMortyApi(engine).searchCharacters("xyz").isEmpty())
    }

    @Test
    fun searchThrowsOnServerError() = runTest {
        val engine = MockEngine {
            respond("""{"error":"boom"}""", HttpStatusCode.InternalServerError, jsonHeaders)
        }

        assertFailsWith<IllegalStateException> { RickMortyApi(engine).searchCharacters("Rick") }
    }

    @Test
    fun getAllCharactersParsesResults() = runTest {
        val engine = MockEngine {
            respond(
                """{"results":[{"id":1,"name":"Rick Sanchez","status":"Alive","species":"Human","image":"x"},{"id":2,"name":"Morty Smith","status":"Alive","species":"Human","image":"y"}]}""",
                HttpStatusCode.OK,
                jsonHeaders
            )
        }

        assertEquals(listOf(1, 2), RickMortyApi(engine).getAllCharacters().map { it.id })
    }

    @Test
    fun getAllCharactersThrowsOnServerError() = runTest {
        val engine = MockEngine { respond("", HttpStatusCode.ServiceUnavailable, jsonHeaders) }

        assertFailsWith<IllegalStateException> { RickMortyApi(engine).getAllCharacters() }
    }
}

package com.example.cvstakehome

import com.example.cvstakehome.data.api.RickAndMortyApi
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.model.CharacterResponse
import com.example.cvstakehome.data.model.Info
import com.example.cvstakehome.data.model.Location
import com.example.cvstakehome.data.model.Origin
import com.example.cvstakehome.data.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for CharacterRepository.
 * Tests API interaction and error handling.
 */
class CharacterRepositoryTest {

    private lateinit var api: RickAndMortyApi
    private lateinit var repository: CharacterRepository

    @Before
    fun setup() {
        api = mockk()
        repository = CharacterRepository(api)
    }

    @Test
    fun `searchCharacters returns success with characters`() = runTest {
        // Given
        val query = "Rick"
        val mockCharacters = listOf(
            createMockCharacter(1, "Rick Sanchez"),
            createMockCharacter(2, "Rick Prime")
        )
        val mockResponse = CharacterResponse(
            info = Info(count = 2, pages = 1, next = null, prev = null),
            results = mockCharacters
        )
        coEvery { api.searchCharacters(query) } returns mockResponse

        // When
        val result = repository.searchCharacters(query)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockCharacters, result.getOrNull())
    }

    @Test
    fun `searchCharacters returns empty list for blank query`() = runTest {
        // Given
        val query = ""

        // When
        val result = repository.searchCharacters(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `searchCharacters returns empty list for whitespace query`() = runTest {
        // Given
        val query = "   "

        // When
        val result = repository.searchCharacters(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `searchCharacters returns failure on API error`() = runTest {
        // Given
        val query = "Rick"
        val exception = Exception("Network error")
        coEvery { api.searchCharacters(query) } throws exception

        // When
        val result = repository.searchCharacters(query)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `searchCharacters handles empty results from API`() = runTest {
        // Given
        val query = "NonExistent"
        val mockResponse = CharacterResponse(
            info = Info(count = 0, pages = 0, next = null, prev = null),
            results = emptyList()
        )
        coEvery { api.searchCharacters(query) } returns mockResponse

        // When
        val result = repository.searchCharacters(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    /**
     * Helper function to create a mock Character for testing.
     */
    private fun createMockCharacter(id: Int, name: String): Character {
        return Character(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Origin("Earth", ""),
            location = Location("Earth", ""),
            image = "https://example.com/image.jpg",
            episode = emptyList(),
            url = "",
            created = "2017-11-04T18:48:46.250Z"
        )
    }
}


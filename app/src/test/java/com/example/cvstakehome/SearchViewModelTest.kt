package com.example.cvstakehome

import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.model.Location
import com.example.cvstakehome.data.model.Origin
import com.example.cvstakehome.data.repository.CharacterRepository
import com.example.cvstakehome.ui.search.SearchUiState
import com.example.cvstakehome.ui.search.SearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SearchViewModel.
 * Tests search functionality, state management, and error handling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Initial`() {
        assertEquals(SearchUiState.Initial, viewModel.uiState.value)
    }

    @Test
    fun `search query is updated correctly`() = runTest {
        val query = "Rick"
        viewModel.onSearchQueryChanged(query)
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `successful search updates state to Success`() = runTest {
        // Given
        val query = "Rick"
        val mockCharacters = listOf(
            createMockCharacter(1, "Rick Sanchez"),
            createMockCharacter(2, "Rick Prime")
        )
        coEvery { repository.searchCharacters(query) } returns Result.success(mockCharacters)

        // When
        viewModel.onSearchQueryChanged(query)
        advanceTimeBy(350) // Wait for debounce
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success)
        assertEquals(mockCharacters, (state as SearchUiState.Success).characters)
    }

    @Test
    fun `empty search result updates state to Empty`() = runTest {
        // Given
        val query = "NonExistentCharacter"
        coEvery { repository.searchCharacters(query) } returns Result.success(emptyList())

        // When
        viewModel.onSearchQueryChanged(query)
        advanceTimeBy(350) // Wait for debounce
        advanceUntilIdle()

        // Then
        assertEquals(SearchUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `failed search updates state to Error`() = runTest {
        // Given
        val query = "Rick"
        val errorMessage = "Network error"
        coEvery { repository.searchCharacters(query) } returns Result.failure(
            Exception(errorMessage)
        )

        // When
        viewModel.onSearchQueryChanged(query)
        advanceTimeBy(350) // Wait for debounce
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
        assertEquals(errorMessage, (state as SearchUiState.Error).message)
    }

    @Test
    fun `search is debounced and only last query is executed`() = runTest {
        // Given
        val mockCharacters = listOf(createMockCharacter(1, "Morty"))
        coEvery { repository.searchCharacters("Morty") } returns Result.success(mockCharacters)
        coEvery { repository.searchCharacters("Mo") } returns Result.success(emptyList())
        coEvery { repository.searchCharacters("Mor") } returns Result.success(emptyList())

        // When - Type "Morty" character by character
        viewModel.onSearchQueryChanged("M")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("Mo")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("Mor")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("Mort")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("Morty")
        advanceTimeBy(350) // Wait for debounce
        advanceUntilIdle()

        // Then - Only the last search should execute
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success)
        assertEquals(mockCharacters, (state as SearchUiState.Success).characters)
    }

    @Test
    fun `loading state is shown during search`() = runTest {
        // Given
        val query = "Rick"
        coEvery { repository.searchCharacters(query) } returns Result.success(emptyList())

        // When
        viewModel.onSearchQueryChanged(query)
        advanceTimeBy(350) // Wait for debounce

        // Then - Before advancing idle, loading state should be present
        assertEquals(SearchUiState.Loading, viewModel.uiState.value)
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


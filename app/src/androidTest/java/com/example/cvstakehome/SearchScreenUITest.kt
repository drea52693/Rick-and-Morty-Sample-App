package com.example.cvstakehome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.model.Location
import com.example.cvstakehome.data.model.Origin
import com.example.cvstakehome.data.repository.CharacterRepository
import com.example.cvstakehome.ui.search.SearchScreen
import com.example.cvstakehome.ui.search.SearchViewModel
import com.example.cvstakehome.ui.theme.CVSTakehomeTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for the SearchScreen.
 * Tests user interactions and UI state changes.
 */
class SearchScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: CharacterRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        repository = mockk()
    }

    @Test
    fun searchScreen_displaysSearchBarAndInitialState() {
        // Given
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Start searching for characters")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysLoadingIndicator() = runTest {
        // Given
        coEvery { repository.searchCharacters(any()) } coAnswers {
            kotlinx.coroutines.delay(1000) // Simulate slow network
            Result.success(emptyList())
        }
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Perform search
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Then - Loading indicator should be visible
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule
                    .onNodeWithContentDescription("Loading")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun searchScreen_displaysCharactersAfterSearch() = runTest {
        // Given
        val mockCharacters = listOf(
            createMockCharacter(1, "Rick Sanchez"),
            createMockCharacter(2, "Morty Smith")
        )
        coEvery { repository.searchCharacters("Rick") } returns Result.success(mockCharacters)
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Perform search
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Then - Wait for results and verify characters are displayed
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule
                    .onNodeWithText("Rick Sanchez")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Morty Smith")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysEmptyStateForNoResults() = runTest {
        // Given
        coEvery { repository.searchCharacters("Zaphod") } returns Result.success(emptyList())
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Perform search
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Zaphod")

        // Then - Wait for empty state
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule
                    .onNodeWithText("No characters found")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("No characters found")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysErrorStateOnFailure() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { repository.searchCharacters("Rick") } returns Result.failure(
            Exception(errorMessage)
        )
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Perform search
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Then - Wait for error state
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule
                    .onNodeWithText("Error: $errorMessage")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Error: $errorMessage")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_clearButton_clearsSearchQuery() = runTest {
        // Given
        coEvery { repository.searchCharacters(any()) } returns Result.success(emptyList())
        viewModel = SearchViewModel(repository)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onCharacterClick = {}
                )
            }
        }

        // Type in search bar
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Wait for clear button to appear
        composeTestRule.waitForIdle()

        // Click clear button
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .performClick()

        // Then - Search should be cleared and initial state shown
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Start searching for characters")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
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
            image = "https://rickandmortyapi.com/api/character/avatar/$id.jpeg",
            episode = emptyList(),
            url = "",
            created = "2017-11-04T18:48:46.250Z"
        )
    }
}


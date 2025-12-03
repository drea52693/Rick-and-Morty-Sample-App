package com.example.cvstakehome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.model.Location
import com.example.cvstakehome.data.model.Origin
import com.example.cvstakehome.data.repository.CharacterRepository
import com.example.cvstakehome.ui.navigation.RickAndMortyNavigation
import com.example.cvstakehome.ui.theme.CVSTakehomeTheme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for navigation between screens.
 * Tests the full user flow from search to detail and back.
 */
class NavigationUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: CharacterRepository

    @Before
    fun setup() {
        repository = mockk()
    }

    @Test
    fun navigation_searchToDetailAndBack() = runTest {
        // Given
        val mockCharacters = listOf(
            createMockCharacter(1, "Rick Sanchez"),
            createMockCharacter(2, "Morty Smith")
        )
        coEvery { repository.searchCharacters("Rick") } returns Result.success(mockCharacters)

        // When - Start app
        composeTestRule.setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation()
            }
        }

        // Then - Should show search screen
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .assertIsDisplayed()

        // When - Perform search
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Wait for results
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

        // When - Tap on a character
        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .performClick()

        // Then - Should navigate to detail screen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Species")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Species")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Human")
            .assertIsDisplayed()

        // When - Press back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then - Should return to search screen with results still visible
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Search Rick and Morty characters...")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_initialScreenIsSearch() {
        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation()
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
    fun navigation_multipleCharacterClicks() = runTest {
        // Given
        val mockCharacters = listOf(
            createMockCharacter(1, "Rick Sanchez"),
            createMockCharacter(2, "Morty Smith")
        )
        coEvery { repository.searchCharacters("Rick") } returns Result.success(mockCharacters)

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation()
            }
        }

        // Search for characters
        composeTestRule
            .onNodeWithText("Search Rick and Morty characters...")
            .performTextInput("Rick")

        // Wait for first character
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

        // Click first character
        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .performClick()

        // Wait for detail screen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Species")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Go back
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Wait for search screen
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Morty Smith")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Click second character
        composeTestRule
            .onNodeWithText("Morty Smith")
            .performClick()

        // Verify detail screen again
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Species")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Species")
            .assertIsDisplayed()
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


package com.example.cvstakehome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.model.Location
import com.example.cvstakehome.data.model.Origin
import com.example.cvstakehome.ui.detail.DetailScreen
import com.example.cvstakehome.ui.theme.CVSTakehomeTheme
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for the DetailScreen.
 * Tests display of character details and user interactions.
 */
class DetailScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailScreen_displaysCharacterName() {
        // Given
        val character = createTestCharacter()

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()
    }

    @Test
    fun detailScreen_displaysAllRequiredFields() {
        // Given
        val character = createTestCharacter()

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Verify all required fields are displayed
        composeTestRule
            .onNodeWithText("Species")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Human")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Status")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Alive")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Origin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Earth (C-137)")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Created")
            .assertIsDisplayed()
    }

    @Test
    fun detailScreen_displaysTypeWhenAvailable() {
        // Given
        val character = createTestCharacter().copy(type = "Scientist")

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Type")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scientist")
            .assertIsDisplayed()
    }

    @Test
    fun detailScreen_hidesTypeWhenEmpty() {
        // Given
        val character = createTestCharacter().copy(type = "")

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Type label should not be visible
        composeTestRule
            .onNodeWithText("Type")
            .assertDoesNotExist()
    }

    @Test
    fun detailScreen_backButton_triggersCallback() {
        // Given
        val character = createTestCharacter()
        var backClicked = false

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = { backClicked = true }
                )
            }
        }

        // Click back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then
        assert(backClicked) { "Back button callback was not triggered" }
    }

    @Test
    fun detailScreen_displaysFormattedDate() {
        // Given
        val character = createTestCharacter().copy(
            created = "2017-11-04T18:48:46.250Z"
        )

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Should display formatted date
        composeTestRule
            .onNodeWithText("Created")
            .assertIsDisplayed()

        // Check that the date is formatted (contains "November" instead of raw ISO)
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("November 4, 2017 at 6:48 PM", substring = true)
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun detailScreen_displaysCharacterImage() {
        // Given
        val character = createTestCharacter()

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Character image should be present (with content description)
        composeTestRule
            .onNodeWithContentDescription("Rick Sanchez")
            .assertExists()
    }

    @Test
    fun detailScreen_allLabelsAreVisible() {
        // Given
        val character = createTestCharacter().copy(type = "Scientist")

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Verify all label texts are visible
        val expectedLabels = listOf("Species", "Status", "Origin", "Type", "Created")

        expectedLabels.forEach { label ->
            composeTestRule
                .onNodeWithText(label)
                .assertIsDisplayed()
        }
    }

    @Test
    fun detailScreen_shareButton_isVisible() {
        // Given
        val character = createTestCharacter()

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Share button should be visible
        composeTestRule
            .onNodeWithContentDescription("Share character")
            .assertIsDisplayed()
    }

    @Test
    fun detailScreen_shareButton_isClickable() {
        // Given
        val character = createTestCharacter()

        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                DetailScreen(
                    character = character,
                    onBackClick = {}
                )
            }
        }

        // Then - Share button should be clickable (won't throw exception)
        composeTestRule
            .onNodeWithContentDescription("Share character")
            .assertIsDisplayed()
            .performClick()
        
        // Note: We can't easily test the actual share intent in UI tests
        // but we verify the button exists and is clickable
    }

    /**
     * Helper function to create a test Character.
     */
    private fun createTestCharacter(): Character {
        return Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = Origin("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
            location = Location("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
            ),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z"
        )
    }
}


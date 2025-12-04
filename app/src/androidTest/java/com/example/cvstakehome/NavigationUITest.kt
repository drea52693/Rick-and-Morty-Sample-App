package com.example.cvstakehome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cvstakehome.ui.navigation.RickAndMortyNavigation
import com.example.cvstakehome.ui.theme.CVSTakehomeTheme
import org.junit.Rule
import org.junit.Test

class NavigationUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun navigation_filterBarIsVisible() {
        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation()
            }
        }

        // Then - Filter section should be visible
        composeTestRule
            .onNodeWithText("Filters")
            .assertIsDisplayed()
    }

    @Test
    fun navigation_filtersCanBeExpanded() {
        // When
        composeTestRule.setContent {
            CVSTakehomeTheme {
                RickAndMortyNavigation()
            }
        }

        // Click "Filters" row to expand filters
        composeTestRule
            .onNodeWithText("Filters")
            .assertIsDisplayed()
            .performClick()

        // Then - Status filter label should appear
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule
                    .onNodeWithText("Status")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule
            .onNodeWithText("Status")
            .assertExists()
    }
}


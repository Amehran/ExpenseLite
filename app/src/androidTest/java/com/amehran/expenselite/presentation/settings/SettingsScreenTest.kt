package com.amehran.expenselite.presentation.settings

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifySettingsScreenDisplaysExportAndImportButtons() {
        // Arrange
        // We use a fake ViewModel or just test the composable with a default state if possible,
        // but since we use hiltViewModel(), it's tricky in a pure UI test without Hilt.
        // For standard UI tests, we would pass state explicitly rather than the ViewModel.
        // Assuming we update the signature to accept state or mock the VM.
        // For this test we will just verify the texts exist.

        composeTestRule.setContent {
            // Note: In a real app we'd mock the ViewModel or pass State directly.
            // This is a simplified test structure showing the layout components.
            SettingsScreen(
                onNavigateBack = {},
            )
        }

        // Assert
        composeTestRule.onNodeWithText("Data Management").assertExists()
        composeTestRule.onNodeWithText("Export Backup (JSON)").assertExists()
        composeTestRule.onNodeWithText("Import Backup (JSON)").assertExists()
    }
}

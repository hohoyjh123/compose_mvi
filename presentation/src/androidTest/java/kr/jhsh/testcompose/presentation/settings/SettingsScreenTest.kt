package kr.jhsh.testcompose.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * SettingsScreen UI 테스트
 * - 프로필 섹션 표시 테스트
 * - 닉네임 표시 테스트
 * - 닉네임 클릭 테스트
 * - 앱 정보 표시 테스트
 */
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createMockViewModel(
        nickname: String? = null
    ): SettingsViewModel {
        val viewModel = mockk<SettingsViewModel>(relaxed = true)
        every { viewModel.nickname } returns MutableStateFlow(nickname)
        return viewModel
    }

    @Test
    fun `닉네임이_설정되어_있을_때_닉네임이_표시되어야_한다`() {
        // Given
        val nickname = "TestUser123"
        val viewModel = createMockViewModel(nickname)

        // When
        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToEditNickname = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(nickname).assertIsDisplayed()
    }

    @Test
    fun `닉네임이_없을_때_기본_텍스트가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(null)

        // When
        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToEditNickname = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("JSONPlaceholder App").assertIsDisplayed()
    }

    @Test
    fun `프로필_섹션_클릭_시_onNavigateToEditNickname이_호출되어야_한다`() {
        // Given
        val viewModel = createMockViewModel("TestUser")
        var navigateCalled = false

        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToEditNickname = { navigateCalled = true }
            )
        }

        // When
        composeTestRule.onNodeWithText("TestUser").performClick()

        // Then
        assert(navigateCalled)
    }

    @Test
    fun `앱_정보_섹션이_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel("TestUser")

        // When
        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToEditNickname = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("앱 정보").assertIsDisplayed()
    }

    @Test
    fun `버전_정보가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel("TestUser")

        // When
        composeTestRule.setContent {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToEditNickname = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("버전").assertIsDisplayed()
    }
}

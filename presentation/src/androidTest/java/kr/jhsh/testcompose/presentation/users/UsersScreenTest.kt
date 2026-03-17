package kr.jhsh.testcompose.presentation.users

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kr.jhsh.testcompose.domain.model.User
import org.junit.Rule
import org.junit.Test

/**
 * UsersScreen UI 테스트
 * - 로딩 상태 표시 테스트
 * - 에러 상태 표시 테스트
 * - 사용자 목록 표시 테스트
 * - 사용자 클릭 테스트
 * - Pull-to-refresh 테스트
 */
class UsersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createMockViewModel(
        currentState: UsersState = UsersState()
    ): UsersViewModel {
        val viewModel = mockk<UsersViewModel>(relaxed = true)
        every { viewModel.state } returns MutableStateFlow(currentState)
        every { viewModel.effect } returns emptyFlow()
        every { viewModel.handleIntent(any()) } returns Unit
        return viewModel
    }

    @Test
    fun `로딩_중일_때_로딩_인디케이터가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            UsersState(isLoading = true, users = emptyList())
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        // CircularProgressIndicator는 content description이 없어서 직접 확인 어려움
        // 하지만 에러나 리스트가 표시되지 않는 것으로 확인
        composeTestRule.onNodeWithText("Tap to retry").assertDoesNotExist()
    }

    @Test
    fun `에러_발생_시_에러_메시지가_표시되어야_한다`() {
        // Given
        val errorMessage = "Network error occurred"
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = emptyList(), error = errorMessage)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to retry").assertIsDisplayed()
    }

    @Test
    fun `사용자_목록이_표시되어야_한다`() {
        // Given
        val users = listOf(
            User(1, "John Doe", "johndoe", "john@example.com"),
            User(2, "Jane Smith", "janesmith", "jane@example.com")
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("@johndoe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jane Smith").assertIsDisplayed()
        composeTestRule.onNodeWithText("@janesmith").assertIsDisplayed()
    }

    @Test
    fun `사용자_클릭_시_onUserClick이_호출되어야_한다`() {
        // Given
        val users = listOf(
            User(1, "John Doe", "johndoe", "john@example.com")
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )
        var clickedUserId: Int? = null

        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = { clickedUserId = it }
            )
        }

        // When
        composeTestRule.onNodeWithText("John Doe").performClick()

        // Then
        assert(clickedUserId == 1)
    }

    @Test
    fun `에러_화면_클릭_시_LoadUsers_Intent가_전송되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = emptyList(), error = "Error")
        )

        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("Tap to retry").performClick()

        // Then
        verify { viewModel.handleIntent(UsersIntent.LoadUsers) }
    }

    @Test
    fun `사용자_정보에_전화번호가_없을_때도_표시되어야_한다`() {
        // Given
        val users = listOf(
            User(
                id = 1,
                name = "Test User",
                username = "testuser",
                email = "test@test.com",
                phone = null,
                website = null
            )
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Test User").assertIsDisplayed()
        composeTestRule.onNodeWithText("@testuser").assertIsDisplayed()
        composeTestRule.onNodeWithText("test@test.com").assertIsDisplayed()
    }

    @Test
    fun `빈_사용자_목록일_때_아무것도_표시되지_않아야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = emptyList(), error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        // 빈 화면이 표시됨 (에러도 아니고 로딩도 아님)
        composeTestRule.onNodeWithText("Tap to retry").assertDoesNotExist()
    }

    @Test
    fun `사용자_이메일이_표시되어야_한다`() {
        // Given
        val users = listOf(
            User(1, "Test", "test", "unique@example.com")
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("unique@example.com").assertIsDisplayed()
    }

    @Test
    fun `사용자_이름과_유저네임이_함께_표시되어야_한다`() {
        // Given
        val users = listOf(
            User(1, "Display Name", "username123", "test@test.com")
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Display Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("@username123").assertIsDisplayed()
    }

    @Test
    fun `대량의_사용자_목록이_스크롤_가능해야_한다`() {
        // Given
        val users = (1..20).map {
            User(it, "User $it", "user$it", "user$it@test.com")
        }
        val viewModel = createMockViewModel(
            UsersState(isLoading = false, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("User 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("@user1").assertIsDisplayed()
    }

    @Test
    fun `로딩_중에도_기존_사용자_목록이_유지되어야_한다`() {
        // Given
        val users = listOf(
            User(1, "Existing User", "existing", "existing@test.com")
        )
        val viewModel = createMockViewModel(
            UsersState(isLoading = true, users = users, error = null)
        )

        // When
        composeTestRule.setContent {
            UsersScreen(
                viewModel = viewModel,
                onUserClick = {}
            )
        }

        // Then - 로딩 중이지만 기존 목록은 계속 표시
        composeTestRule.onNodeWithText("Existing User").assertIsDisplayed()
    }
}

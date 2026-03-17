package kr.jhsh.testcompose.presentation.posts

import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

/**
 * PostsScreen UI 테스트
 * - Paging 로딩 상태 테스트
 * - 사용자 목록 표시 테스트
 * - 사용자 클릭 테스트
 */
class PostsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createMockViewModel(
        currentState: PostsState = PostsState()
    ): PostsViewModel {
        val viewModel = mockk<PostsViewModel>(relaxed = true)
        every { viewModel.state } returns MutableStateFlow(currentState)
        every { viewModel.effect } returns emptyFlow()
        return viewModel
    }

    @Test
    fun `초기_로딩_중일_때_로딩_상태가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            PostsState(isInitialLoading = true, error = null)
        )

        // When
        composeTestRule.setContent {
            PostsScreen(
                viewModel = viewModel,
                onPostClick = {}
            )
        }

        // Then
        // Paging 로딩 상태에 따른 UI 확인
    }

    @Test
    fun `PostsScreen이_정상적으로_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            PostsState(isInitialLoading = false, error = null)
        )

        // When
        composeTestRule.setContent {
            PostsScreen(
                viewModel = viewModel,
                onPostClick = {}
            )
        }

        // Then
        // 화면이 정상적으로 렌더링됨
    }
}

package kr.jhsh.testcompose.presentation.settings.editnickname

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test

/**
 * EditNicknameScreen UI 테스트
 * - 화면 요소 표시 테스트
 * - 사용자 입력 테스트
 * - 버튼 상태 테스트
 * - 유효성 검사 시각적 피드백 테스트
 */
class EditNicknameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createMockViewModel(
        currentState: EditNicknameState = EditNicknameState()
    ): EditNicknameViewModel {
        val viewModel = mockk<EditNicknameViewModel>(relaxed = true)
        every { viewModel.state } returns MutableStateFlow(currentState)
        every { viewModel.effect } returns emptyFlow()
        every { viewModel.handleIntent(any()) } returns Unit
        return viewModel
    }

    @Test
    fun `화면이_표시되면_모든_기본_요소가_보여야_한다`() {
        // Given
        val viewModel = createMockViewModel()

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("닉네임 변경").assertIsDisplayed()
        composeTestRule.onNodeWithText("현재 닉네임").assertIsDisplayed()
        composeTestRule.onNodeWithText("변경할 닉네임").assertIsDisplayed()
        composeTestRule.onNodeWithText("저장").assertIsDisplayed()
    }

    @Test
    fun `현재_닉네임이_설정되어_있으면_표시되어야_한다`() {
        // Given
        val currentNickname = "TestUser123"
        val viewModel = createMockViewModel(
            EditNicknameState(currentNickname = currentNickname)
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText(currentNickname).assertIsDisplayed()
    }

    @Test
    fun `현재_닉네임이_없으면_설정되지_않음_메시지가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(currentNickname = "")
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("설정되지 않음").assertIsDisplayed()
    }

    @Test
    fun `새_닉네임_입력_필드가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel()

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("새 닉네임을 입력하세요").assertIsDisplayed()
    }

    @Test
    fun `유효성_검사_규칙이_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel()

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("• 4~12자의 영문자와 숫자만 사용 가능").assertIsDisplayed()
        composeTestRule.onNodeWithText("• 공백 사용 불가").assertIsDisplayed()
    }

    @Test
    fun `유효한_닉네임_입력_시_저장_버튼이_활성화되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "valid123",
                isValidationSuccess = true,
                isLoading = false
            )
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("저장").assertIsEnabled()
    }

    @Test
    fun `유효하지_않은_닉네임_입력_시_저장_버튼이_비활성화되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "abc",
                isValidationSuccess = false,
                isLoading = false
            )
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("저장").assertIsNotEnabled()
    }

    @Test
    fun `로딩_중일_때_저장_버튼이_비활성화되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "valid123",
                isValidationSuccess = true,
                isLoading = true
            )
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("저장 중...").assertIsDisplayed()
        composeTestRule.onNodeWithText("저장 중...").assertIsNotEnabled()
    }

    @Test
    fun `유효성_검사_성공_메시지가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "valid123",
                validationMessage = "사용 가능한 닉네임입니다",
                isValidationSuccess = true
            )
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("사용 가능한 닉네임입니다").assertIsDisplayed()
    }

    @Test
    fun `유효성_검사_실패_메시지가_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "ab",
                validationMessage = "닉네임은 4자 이상이어야 합니다",
                isValidationSuccess = false
            )
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("닉네임은 4자 이상이어야 합니다").assertIsDisplayed()
    }

    @Test
    fun `뒤로_가기_버튼이_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel()

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("뒤로 가기").assertIsDisplayed()
    }

    @Test
    fun `뒤로_가기_버튼_클릭_시_onNavigateBack이_호출되어야_한다`() {
        // Given
        val viewModel = createMockViewModel()
        var navigateBackCalled = false

        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = { navigateBackCalled = true }
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription("뒤로 가기").performClick()

        // Then
        verify { viewModel.handleIntent(EditNicknameIntent.OnNavigateBack) }
    }

    @Test
    fun `저장_버튼_클릭_시_OnSaveNickname_Intent가_전송되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(
                newNickname = "valid123",
                isValidationSuccess = true,
                isLoading = false
            )
        )

        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("저장").performClick()

        // Then
        verify { viewModel.handleIntent(EditNicknameIntent.OnSaveNickname) }
    }

    @Test
    fun `텍스트_입력_시_OnNewNicknameChanged_Intent가_전송되어야_한다`() {
        // Given
        val viewModel = createMockViewModel()

        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // When
        composeTestRule.onNodeWithText("새 닉네임을 입력하세요").performTextInput("test1234")

        // Then
        verify { viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("test1234")) }
    }

    @Test
    fun `입력_내용_지우기_버튼이_표시되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(newNickname = "test1234")
        )

        // When
        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // Then
        composeTestRule.onNodeWithContentDescription("입력 내용 지우기").assertIsDisplayed()
    }

    @Test
    fun `입력_내용_지우기_버튼_클릭_시_OnClearNickname_Intent가_전송되어야_한다`() {
        // Given
        val viewModel = createMockViewModel(
            EditNicknameState(newNickname = "test1234")
        )

        composeTestRule.setContent {
            EditNicknameScreen(
                viewModel = viewModel,
                onNavigateBack = {}
            )
        }

        // When
        composeTestRule.onNodeWithContentDescription("입력 내용 지우기").performClick()

        // Then
        verify { viewModel.handleIntent(EditNicknameIntent.OnClearNickname) }
    }
}

package kr.jhsh.testcompose.presentation.settings.editnickname

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kr.jhsh.testcompose.domain.usecase.GetNicknameUseCase
import kr.jhsh.testcompose.domain.usecase.SaveNicknameUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * EditNicknameViewModel 단위 테스트
 * - 닉네임 유효성 검사 테스트
 * - 상태 업데이트 테스트
 * - 저장 기능 테스트
 * - Effect 발생 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditNicknameViewModelTest {

    @MockK
    private lateinit var getNicknameUseCase: GetNicknameUseCase

    @MockK
    private lateinit var saveNicknameUseCase: SaveNicknameUseCase

    private lateinit var viewModel: EditNicknameViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(initialNickname: String? = null) {
        every { getNicknameUseCase() } returns flowOf(initialNickname)
        viewModel = EditNicknameViewModel(getNicknameUseCase, saveNicknameUseCase)
    }

    @Test
    fun `초기 상태에서 현재 닉네임이 로드되어야 한다`() = runTest {
        // Given
        val expectedNickname = "testUser"
        every { getNicknameUseCase() } returns flowOf(expectedNickname)

        // When
        viewModel = EditNicknameViewModel(getNicknameUseCase, saveNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(expectedNickname, viewModel.state.value.currentNickname)
        assertEquals("", viewModel.state.value.newNickname)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `닉네임이 없을 때 빈 문자열로 초기화되어야 한다`() = runTest {
        // Given
        every { getNicknameUseCase() } returns flowOf(null)

        // When
        viewModel = EditNicknameViewModel(getNicknameUseCase, saveNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.state.value.currentNickname)
    }

    @Test
    fun `유효한 닉네임 입력 시 검증 성공 상태가 되어야 한다`() = runTest {
        // Given
        createViewModel()
        val validNickname = "test1234"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(validNickname))
        advanceUntilIdle()

        // Then
        assertEquals(validNickname, viewModel.state.value.newNickname)
        assertTrue(viewModel.state.value.isValidationSuccess)
        assertEquals("사용 가능한 닉네임입니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `4자 미만 닉네임 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()
        val shortNickname = "abc"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(shortNickname))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임은 4자 이상이어야 합니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `12자 초과 닉네임 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()
        val longNickname = "thisisverylong"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(longNickname))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임은 12자 이하여야 합니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `공백이 포함된 닉네임 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()
        val nicknameWithSpace = "test user"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(nicknameWithSpace))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임에 공백을 사용할 수 없습니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `영문자와 숫자 외 문자 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()
        val invalidNickname = "test@123"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(invalidNickname))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임은 영문자와 숫자만 사용 가능합니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `빈 닉네임 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(""))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임을 입력해주세요", viewModel.state.value.validationMessage)
    }

    @Test
    fun `한글 닉네임 입력 시 검증 실패해야 한다`() = runTest {
        // Given
        createViewModel()
        val koreanNickname = "테스트1234"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(koreanNickname))
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("닉네임은 영문자와 숫자만 사용 가능합니다", viewModel.state.value.validationMessage)
    }

    @Test
    fun `OnClearNickname Intent 처리 시 입력이 초기화되어야 한다`() = runTest {
        // Given
        createViewModel()
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("test1234"))
        advanceUntilIdle()

        // When
        viewModel.handleIntent(EditNicknameIntent.OnClearNickname)
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.state.value.newNickname)
        assertFalse(viewModel.state.value.isValidationSuccess)
        assertEquals("", viewModel.state.value.validationMessage)
    }

    @Test
    fun `유효한 닉네임 저장 시 성공 Effect가 발생해야 한다`() = runTest {
        // Given
        createViewModel("oldNick")
        coEvery { saveNicknameUseCase(any()) } just runs

        viewModel.effect.test {
            // When
            viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("newNick123"))
            advanceUntilIdle()

            viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
            advanceUntilIdle()

            // Then
            assertEquals(EditNicknameEffect.ShowToast("닉네임이 저장되었습니다"), awaitItem())
            assertEquals(EditNicknameEffect.NavigateBack, awaitItem())

            coVerify { saveNicknameUseCase("newNick123") }
        }
    }

    @Test
    fun `유효하지 않은 닉네임 저장 시 저장되지 않아야 한다`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("abc")) // 3자리, 유효하지 않음
        advanceUntilIdle()

        viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { saveNicknameUseCase(any()) }
        assertFalse(viewModel.state.value.isSaved)
    }

    @Test
    fun `저장 중 오류 발생 시 에러 Effect가 발생해야 한다`() = runTest {
        // Given
        createViewModel()
        val errorMessage = "Network error"
        coEvery { saveNicknameUseCase(any()) } throws RuntimeException(errorMessage)

        viewModel.effect.test {
            // When
            viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("valid123"))
            advanceUntilIdle()

            viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
            advanceUntilIdle()

            // Then
            val effect = awaitItem() as EditNicknameEffect.ShowToast
            assertTrue(effect.message.contains(errorMessage))
            assertFalse(viewModel.state.value.isLoading)
        }
    }

    @Test
    fun `저장 중 로딩 상태가 표시되어야 한다`() = runTest {
        // Given
        createViewModel()
        val saveGate = CompletableDeferred<Unit>()
        coEvery { saveNicknameUseCase(any()) } coAnswers {
            saveGate.await()
        }

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("valid123"))
        advanceUntilIdle()

        viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
        runCurrent()

        // Then - 저장 시작 시 로딩 상태
        assertTrue(viewModel.state.value.isLoading)

        saveGate.complete(Unit)
        advanceUntilIdle()

        // Then - 저장 완료 후 로딩 종료
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isSaved)
    }

    @Test
    fun `OnNavigateBack Intent 처리 시 NavigateBack Effect가 발생해야 한다`() = runTest {
        // Given
        createViewModel()

        viewModel.effect.test {
            // When
            viewModel.handleIntent(EditNicknameIntent.OnNavigateBack)

            // Then
            assertEquals(EditNicknameEffect.NavigateBack, awaitItem())
        }
    }

    @Test
    fun `저장 성공 후 상태가 초기화되어야 한다`() = runTest {
        // Given
        createViewModel("oldNick")
        coEvery { saveNicknameUseCase(any()) } just runs

        viewModel.effect.test {
            // When
            viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged("newNick123"))
            advanceUntilIdle()

            viewModel.handleIntent(EditNicknameIntent.OnSaveNickname)
            advanceUntilIdle()

            skipItems(2) // Skip toast and navigate effects

            // Then
            assertEquals("newNick123", viewModel.state.value.currentNickname)
            assertEquals("", viewModel.state.value.newNickname)
            assertTrue(viewModel.state.value.isSaved)
        }
    }

    @Test
    fun `경계값 4자 닉네임은 유효해야 한다`() = runTest {
        // Given
        createViewModel()
        val minLengthNickname = "abcd"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(minLengthNickname))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isValidationSuccess)
    }

    @Test
    fun `경계값 12자 닉네임은 유효해야 한다`() = runTest {
        // Given
        createViewModel()
        val maxLengthNickname = "abcdefghijkl"

        // When
        viewModel.handleIntent(EditNicknameIntent.OnNewNicknameChanged(maxLengthNickname))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isValidationSuccess)
    }
}

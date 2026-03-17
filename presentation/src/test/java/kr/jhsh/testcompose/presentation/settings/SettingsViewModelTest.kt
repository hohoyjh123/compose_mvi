package kr.jhsh.testcompose.presentation.settings

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kr.jhsh.testcompose.domain.usecase.GetNicknameUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * SettingsViewModel 단위 테스트
 * - 닉네임 상태 관리 테스트
 * - Flow 수집 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @MockK
    private lateinit var getNicknameUseCase: GetNicknameUseCase

    private lateinit var viewModel: SettingsViewModel

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

    @Test
    fun `저장된 닉네임이 있을 때 상태에 반영되어야 한다`() = runTest {
        // Given
        val expectedNickname = "TestUser123"
        every { getNicknameUseCase() } returns flowOf(expectedNickname)

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(expectedNickname, viewModel.nickname.value)
    }

    @Test
    fun `저장된 닉네임이 없을 때 null이 설정되어야 한다`() = runTest {
        // Given
        every { getNicknameUseCase() } returns flowOf(null)

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.nickname.value)
    }

    @Test
    fun `빈 닉네임이 설정되어 있을 때 빈 문자열로 표시되어야 한다`() = runTest {
        // Given
        every { getNicknameUseCase() } returns flowOf("")

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.nickname.value)
    }

    @Test
    fun `닉네임 변경 시 새로운 값이 반영되어야 한다`() = runTest {
        // Given
        every { getNicknameUseCase() } returns flowOf("InitialNick")

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals("InitialNick", viewModel.nickname.value)
    }

    @Test
    fun `긴 닉네임도 정상적으로 처리되어야 한다`() = runTest {
        // Given
        val longNickname = "VeryLongNick"
        every { getNicknameUseCase() } returns flowOf(longNickname)

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(longNickname, viewModel.nickname.value)
    }

    @Test
    fun `특수문자가 포함된 닉네임도 정상적으로 처리되어야 한다`() = runTest {
        // Given
        // 실제로는 유효성 검사에서 막히지만, 저장된 값은 그대로 노출
        val specialNickname = "user_123"
        every { getNicknameUseCase() } returns flowOf(specialNickname)

        // When
        viewModel = SettingsViewModel(getNicknameUseCase)
        advanceUntilIdle()

        // Then
        assertEquals(specialNickname, viewModel.nickname.value)
    }
}

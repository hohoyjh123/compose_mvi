package kr.jhsh.testcompose.presentation.base

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * BaseViewModel 단위 테스트
 * - 상태 관리 테스트
 * - Effect 전송 테스트
 * - Intent 처리 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태가 정상적으로 설정되어야 한다`() = runTest {
        // Given
        val initialState = TestState(count = 0, message = "initial")
        val viewModel = TestViewModel(initialState)

        // Then
        assertEquals(initialState, viewModel.state.value)
    }

    @Test
    fun `setState를 통해 상태가 업데이트되어야 한다`() = runTest {
        // Given
        val viewModel = TestViewModel(TestState())

        // When
        viewModel.incrementCount()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `setState가 여러 번 호출되면 상태가 누적되어야 한다`() = runTest {
        // Given
        val viewModel = TestViewModel(TestState())

        // When
        viewModel.incrementCount()
        viewModel.incrementCount()
        viewModel.updateMessage("updated")
        advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.state.value.count)
        assertEquals("updated", viewModel.state.value.message)
    }

    @Test
    fun `sendEffect를 통해 Effect가 전송되어야 한다`() = runTest {
        // Given
        val viewModel = TestViewModel(TestState())

        // Then
        viewModel.effect.test {
            // When
            viewModel.sendTestEffect(TestEffect.ShowToast("test message"))

            // Then
            assertEquals(TestEffect.ShowToast("test message"), awaitItem())
        }
    }

    @Test
    fun `handleIntent가 Intent를 처리해야 한다`() = runTest {
        // Given
        val viewModel = TestViewModel(TestState())

        // When
        viewModel.handleIntent(TestIntent.Increment)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.count)
    }

    @Test
    fun `복잡한 Intent 처리가 정상적으로 동작해야 한다`() = runTest {
        // Given
        val viewModel = TestViewModel(TestState())

        viewModel.effect.test {
            // When
            viewModel.handleIntent(TestIntent.ShowMessage("hello"))
            advanceUntilIdle()

            // Then
            assertEquals(TestEffect.ShowToast("hello"), awaitItem())
            assertEquals("hello", viewModel.state.value.message)
        }
    }

    // 테스트용 데이터 클래스
    data class TestState(
        val count: Int = 0,
        val message: String = ""
    ) : UiState

    sealed class TestIntent : UiIntent {
        data object Increment : TestIntent()
        data class ShowMessage(val message: String) : TestIntent()
    }

    sealed class TestEffect : UiEffect {
        data class ShowToast(val message: String) : TestEffect()
    }

    // 테스트용 ViewModel 구현
    class TestViewModel(initialState: TestState) :
        BaseViewModel<TestState, TestIntent, TestEffect>(initialState) {

        fun incrementCount() {
            setState { copy(count = count + 1) }
        }

        fun updateMessage(newMessage: String) {
            setState { copy(message = newMessage) }
        }

        fun sendTestEffect(effect: TestEffect) {
            sendEffect(effect)
        }

        override fun handleIntent(intent: TestIntent) {
            when (intent) {
                is TestIntent.Increment -> incrementCount()
                is TestIntent.ShowMessage -> {
                    updateMessage(intent.message)
                    sendEffect(TestEffect.ShowToast(intent.message))
                }
            }
        }
    }
}

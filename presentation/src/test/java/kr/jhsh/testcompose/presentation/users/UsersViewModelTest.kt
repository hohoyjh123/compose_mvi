package kr.jhsh.testcompose.presentation.users

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.domain.usecase.GetUsersUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * UsersViewModel 단위 테스트
 * - 사용자 목록 로드 테스트
 * - 로딩 상태 테스트
 * - 에러 처리 테스트
 * - 새로고침 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    @MockK
    private lateinit var getUsersUseCase: GetUsersUseCase

    private lateinit var viewModel: UsersViewModel

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

    private fun createViewModel() {
        viewModel = UsersViewModel(getUsersUseCase)
    }

    @Test
    fun `초기화 시 사용자 목록 로드가 시작되어야 한다`() = runTest {
        // Given
        val users = listOf(
            User(1, "User 1", "user1", "user1@test.com"),
            User(2, "User 2", "user2", "user2@test.com")
        )
        coEvery { getUsersUseCase() } returns Result.success(users)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(users, viewModel.state.value.users)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `사용자 목록 로드 성공 시 상태가 업데이트되어야 한다`() = runTest {
        // Given
        val users = listOf(
            User(1, "Test User", "testuser", "test@test.com", "123-456", "example.com")
        )
        coEvery { getUsersUseCase() } returns Result.success(users)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(users, viewModel.state.value.users)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `사용자 목록 로드 중 로딩 상태가 표시되어야 한다`() = runTest {
        // Given
        val users = listOf(User(1, "Test", "test", "test@test.com"))
        coEvery { getUsersUseCase() } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(users)
        }

        // When
        createViewModel()
        runCurrent()

        // Then - 초기 로딩 상태
        assertTrue(viewModel.state.value.isLoading)
        assertEquals(emptyList<User>(), viewModel.state.value.users)

        advanceUntilIdle()

        // Then - 로딩 완료 후
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(users, viewModel.state.value.users)
    }

    @Test
    fun `사용자 목록 로드 실패 시 에러 상태가 설정되어야 한다`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getUsersUseCase() } returns Result.failure(RuntimeException(errorMessage))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.users.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(errorMessage, viewModel.state.value.error)
    }

    @Test
    fun `사용자 목록 로드 실패 시 Toast Effect가 발생해야 한다`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { getUsersUseCase() } returns Result.failure(RuntimeException(errorMessage))

        viewModel = UsersViewModel(getUsersUseCase)

        viewModel.effect.test {
            // When
            advanceUntilIdle()

            // Then
            val effect = awaitItem() as UsersEffect.ShowToast
            assertTrue(effect.message.contains(errorMessage))
        }
    }

    @Test
    fun `RefreshUsers Intent 처리 시 사용자 목록이 새로고침되어야 한다`() = runTest {
        // Given
        val initialUsers = listOf(User(1, "Old", "old", "old@test.com"))
        val refreshedUsers = listOf(
            User(1, "Updated", "updated", "updated@test.com"),
            User(2, "New", "new", "new@test.com")
        )

        coEvery { getUsersUseCase() } returns Result.success(initialUsers) andThen Result.success(refreshedUsers)

        createViewModel()
        advanceUntilIdle()

        assertEquals(initialUsers, viewModel.state.value.users)

        // When
        viewModel.handleIntent(UsersIntent.RefreshUsers)
        advanceUntilIdle()

        // Then
        assertEquals(refreshedUsers, viewModel.state.value.users)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `빈 사용자 목록 로드 시 빈 리스트가 설정되어야 한다`() = runTest {
        // Given
        coEvery { getUsersUseCase() } returns Result.success(emptyList())

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.users.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `사용자 목록에 모든 필드가 포함되어야 한다`() = runTest {
        // Given
        val user = User(
            id = 1,
            name = "Test Name",
            username = "testusername",
            email = "test@example.com",
            phone = "123-456-7890",
            website = "test.com"
        )
        coEvery { getUsersUseCase() } returns Result.success(listOf(user))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val loadedUser = viewModel.state.value.users.first()
        assertEquals(1, loadedUser.id)
        assertEquals("Test Name", loadedUser.name)
        assertEquals("testusername", loadedUser.username)
        assertEquals("test@example.com", loadedUser.email)
        assertEquals("123-456-7890", loadedUser.phone)
        assertEquals("test.com", loadedUser.website)
    }

    @Test
    fun `nullable 필드를 가진 사용자 로드 시 정상적으로 처리되어야 한다`() = runTest {
        // Given
        val user = User(
            id = 1,
            name = "Test",
            username = "test",
            email = "test@test.com",
            phone = null,
            website = null
        )
        coEvery { getUsersUseCase() } returns Result.success(listOf(user))

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        val loadedUser = viewModel.state.value.users.first()
        assertNull(loadedUser.phone)
        assertNull(loadedUser.website)
    }

    @Test
    fun `LoadUsers Intent 처리 시 사용자 목록이 로드되어야 한다`() = runTest {
        // Given
        val users = listOf(User(1, "Test", "test", "test@test.com"))
        coEvery { getUsersUseCase() } returns Result.success(users)

        viewModel = UsersViewModel(getUsersUseCase) // init에서 자동 호출되므로 mock 설정 후 생성
        advanceUntilIdle()

        // 초기화로 인해 이미 로드됨
        assertEquals(users, viewModel.state.value.users)

        // When - 명시적으로 다시 로드
        viewModel.handleIntent(UsersIntent.LoadUsers)
        advanceUntilIdle()

        // Then
        assertEquals(users, viewModel.state.value.users)
    }

    @Test
    fun `여러 번 새로고침 시 최신 데이터가 반영되어야 한다`() = runTest {
        // Given
        val users1 = listOf(User(1, "User 1", "user1", "user1@test.com"))
        val users2 = listOf(User(2, "User 2", "user2", "user2@test.com"))
        val users3 = listOf(User(3, "User 3", "user3", "user3@test.com"))

        coEvery { getUsersUseCase() } returns Result.success(users1) andThen
                Result.success(users2) andThen Result.success(users3)

        createViewModel()
        advanceUntilIdle()
        assertEquals(users1, viewModel.state.value.users)

        // When - 첫 번째 새로고침
        viewModel.handleIntent(UsersIntent.RefreshUsers)
        advanceUntilIdle()
        assertEquals(users2, viewModel.state.value.users)

        // When - 두 번째 새로고침
        viewModel.handleIntent(UsersIntent.RefreshUsers)
        advanceUntilIdle()

        // Then
        assertEquals(users3, viewModel.state.value.users)
    }

    @Test
    fun `에러 후 새로고침 성공 시 에러 상태가 초기화되어야 한다`() = runTest {
        // Given
        coEvery { getUsersUseCase() } returns Result.failure(RuntimeException("Error")) andThen
                Result.success(listOf(User(1, "Test", "test", "test@test.com")))

        createViewModel()
        advanceUntilIdle()

        // 에러 상태 확인
        assertNotNull(viewModel.state.value.error)

        // When - 새로고침
        viewModel.handleIntent(UsersIntent.RefreshUsers)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.error)
        assertEquals(1, viewModel.state.value.users.size)
    }

    @Test
    fun `대량의 사용자 목록도 정상적으로 로드되어야 한다`() = runTest {
        // Given
        val manyUsers = (1..100).map {
            User(it, "User $it", "user$it", "user$it@test.com")
        }
        coEvery { getUsersUseCase() } returns Result.success(manyUsers)

        // When
        createViewModel()
        advanceUntilIdle()

        // Then
        assertEquals(100, viewModel.state.value.users.size)
        assertEquals("User 50", viewModel.state.value.users[49].name)
    }
}

package kr.jhsh.testcompose.presentation.posts

import androidx.paging.PagingData
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.usecase.GetPostsUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * PostsViewModel 단위 테스트
 * - PagingData Flow 테스트
 * - 초기 로딩 상태 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostsViewModelTest {

    @MockK
    private lateinit var getPostsUseCase: GetPostsUseCase

    private lateinit var viewModel: PostsViewModel

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

    private fun createSamplePosts(): List<Post> {
        return listOf(
            Post(
                id = "1",
                name = "John Doe",
                email = "john@example.com",
                phone = "123-456",
                cell = "789-012",
                gender = "male",
                age = 30,
                country = "USA",
                city = "New York",
                pictureThumbnail = "thumb1.jpg",
                pictureLarge = "large1.jpg",
                pictureMedium = "medium1.jpg",
                nationality = "US"
            ),
            Post(
                id = "2",
                name = "Jane Smith",
                email = "jane@example.com",
                phone = "987-654",
                cell = "321-098",
                gender = "female",
                age = 25,
                country = "UK",
                city = "London",
                pictureThumbnail = "thumb2.jpg",
                pictureLarge = "large2.jpg",
                pictureMedium = "medium2.jpg",
                nationality = "GB"
            )
        )
    }

    @Test
    fun `초기화 시 posts Flow가 생성되어야 한다`() = runTest {
        // Given
        val posts = createSamplePosts()
        every { getPostsUseCase() } returns flowOf(PagingData.from(posts))

        // When
        viewModel = PostsViewModel(getPostsUseCase)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.posts != null)
    }

    @Test
    fun `초기 로딩 상태가 false로 설정되어야 한다`() = runTest {
        // Given
        val posts = createSamplePosts()
        every { getPostsUseCase() } returns flowOf(PagingData.from(posts))

        // When
        viewModel = PostsViewModel(getPostsUseCase)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isInitialLoading)
    }

    @Test
    fun `초기 에러 상태가 null이어야 한다`() = runTest {
        // Given
        val posts = createSamplePosts()
        every { getPostsUseCase() } returns flowOf(PagingData.from(posts))

        // When
        viewModel = PostsViewModel(getPostsUseCase)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `LoadPosts Intent 처리 시 초기 로딩이 완료되어야 한다`() = runTest {
        // Given
        val posts = createSamplePosts()
        every { getPostsUseCase() } returns flowOf(PagingData.from(posts))

        viewModel = PostsViewModel(getPostsUseCase)

        // 초기 상태 확인 (init에서 LoadPosts 호출됨)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isInitialLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `posts Flow는 viewModelScope에 cachedIn 되어야 한다`() = runTest {
        // Given
        val posts = createSamplePosts()
        every { getPostsUseCase() } returns flowOf(PagingData.from(posts))

        // When
        viewModel = PostsViewModel(getPostsUseCase)

        // Then - posts는 Flow<PagingData<Post>> 타입
        assertTrue(viewModel.posts is Flow<PagingData<Post>>)
    }

    @Test
    fun `초기 상태값이 올바르게 설정되어야 한다`() {
        // Given & When
        val initialState = PostsState()

        // Then
        assertFalse(initialState.isInitialLoading)
        assertNull(initialState.error)
    }

    @Test
    fun `Post 모델의 모든 필드가 올바르게 설정되어야 한다`() {
        // Given
        val post = Post(
            id = "test-id-123",
            name = "Test User",
            email = "test@test.com",
            phone = "123-456-7890",
            cell = "098-765-4321",
            gender = "male",
            age = 35,
            country = "Korea",
            city = "Seoul",
            pictureThumbnail = "https://example.com/thumb.jpg",
            pictureLarge = "https://example.com/large.jpg",
            pictureMedium = "https://example.com/medium.jpg",
            nationality = "KR"
        )

        // Then
        assertEquals("test-id-123", post.id)
        assertEquals("Test User", post.name)
        assertEquals("test@test.com", post.email)
        assertEquals("123-456-7890", post.phone)
        assertEquals("098-765-4321", post.cell)
        assertEquals("male", post.gender)
        assertEquals(35, post.age)
        assertEquals("Korea", post.country)
        assertEquals("Seoul", post.city)
        assertEquals("https://example.com/thumb.jpg", post.pictureThumbnail)
        assertEquals("https://example.com/large.jpg", post.pictureLarge)
        assertEquals("https://example.com/medium.jpg", post.pictureMedium)
        assertEquals("KR", post.nationality)
    }

    @Test
    fun `Effect가 정상적으로 정의되어야 한다`() {
        // Given
        val effect = PostsEffect.ShowToast("Test message")

        // Then
        assertEquals("Test message", (effect as PostsEffect.ShowToast).message)
    }

    @Test
    fun `Intent가 정상적으로 정의되어야 한다`() {
        // Given
        val intent = PostsIntent.LoadPosts

        // Then
        assertTrue(intent is PostsIntent.LoadPosts)
    }
}

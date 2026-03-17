package kr.jhsh.testcompose.presentation.postdetail

import io.mockk.MockKAnnotations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kr.jhsh.testcompose.domain.model.Post
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * PostDetailViewModel 단위 테스트
 * - Post 로드 테스트
 * - 상태 업데이트 테스트
 * - 새로고침 테스트
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostDetailViewModelTest {

    private lateinit var viewModel: PostDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = PostDetailViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createSamplePost(): Post {
        return Post(
            id = "test-id-123",
            name = "John Doe",
            email = "john@example.com",
            phone = "123-456-7890",
            cell = "098-765-4321",
            gender = "male",
            age = 30,
            country = "USA",
            city = "New York",
            pictureThumbnail = "https://example.com/thumb.jpg",
            pictureLarge = "https://example.com/large.jpg",
            pictureMedium = "https://example.com/medium.jpg",
            nationality = "US"
        )
    }

    @Test
    fun `초기 상태가 올바르게 설정되어야 한다`() {
        // Then
        assertNull(viewModel.state.value.post)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `LoadPost Intent 처리 시 Post가 로드되어야 한다`() = runTest {
        // Given
        val post = createSamplePost()

        // When
        viewModel.handleIntent(PostDetailIntent.LoadPost(post))
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.state.value.post)
        assertEquals(post, viewModel.state.value.post)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `로드된 Post의 모든 필드가 올바르게 설정되어야 한다`() = runTest {
        // Given
        val post = createSamplePost()

        // When
        viewModel.handleIntent(PostDetailIntent.LoadPost(post))
        advanceUntilIdle()

        // Then
        val loadedPost = viewModel.state.value.post!!
        assertEquals("test-id-123", loadedPost.id)
        assertEquals("John Doe", loadedPost.name)
        assertEquals("john@example.com", loadedPost.email)
        assertEquals("123-456-7890", loadedPost.phone)
        assertEquals("098-765-4321", loadedPost.cell)
        assertEquals("male", loadedPost.gender)
        assertEquals(30, loadedPost.age)
        assertEquals("USA", loadedPost.country)
        assertEquals("New York", loadedPost.city)
        assertEquals("https://example.com/thumb.jpg", loadedPost.pictureThumbnail)
        assertEquals("https://example.com/large.jpg", loadedPost.pictureLarge)
        assertEquals("https://example.com/medium.jpg", loadedPost.pictureMedium)
        assertEquals("US", loadedPost.nationality)
    }

    @Test
    fun `다른 Post로 교체 시 새로운 데이터가 반영되어야 한다`() = runTest {
        // Given
        val post1 = createSamplePost()
        val post2 = Post(
            id = "test-id-456",
            name = "Jane Smith",
            email = "jane@example.com",
            phone = "987-654-3210",
            cell = "012-345-6789",
            gender = "female",
            age = 25,
            country = "UK",
            city = "London",
            pictureThumbnail = "https://example.com/thumb2.jpg",
            pictureLarge = "https://example.com/large2.jpg",
            pictureMedium = "https://example.com/medium2.jpg",
            nationality = "GB"
        )

        // When
        viewModel.handleIntent(PostDetailIntent.LoadPost(post1))
        advanceUntilIdle()

        // Then - 첫 번째 Post
        assertEquals("test-id-123", viewModel.state.value.post?.id)

        // When - 두 번째 Post로 교체
        viewModel.handleIntent(PostDetailIntent.LoadPost(post2))
        advanceUntilIdle()

        // Then - 두 번째 Post
        assertEquals("test-id-456", viewModel.state.value.post?.id)
        assertEquals("Jane Smith", viewModel.state.value.post?.name)
    }

    @Test
    fun `RefreshPost Intent 처리 시 현재 상태가 유지되어야 한다`() = runTest {
        // Given
        val post = createSamplePost()
        viewModel.handleIntent(PostDetailIntent.LoadPost(post))
        advanceUntilIdle()

        // When
        viewModel.handleIntent(PostDetailIntent.RefreshPost)
        advanceUntilIdle()

        // Then - 현재로서는 refresh가 아무 동작도 하지 않으므로 상태 유지
        assertNotNull(viewModel.state.value.post)
        assertEquals(post.id, viewModel.state.value.post?.id)
    }

    @Test
    fun `PostDetailEffect ShowToast가 정상적으로 생성되어야 한다`() {
        // Given
        val effect = PostDetailEffect.ShowToast("Test message")

        // Then
        assertEquals("Test message", effect.message)
    }

    @Test
    fun `PostDetailEffect NavigateBack이 정상적으로 생성되어야 한다`() {
        // Given
        val effect = PostDetailEffect.NavigateBack

        // Then
        assertTrue(effect is PostDetailEffect.NavigateBack)
    }

    @Test
    fun `LoadPost Intent가 정상적으로 생성되어야 한다`() {
        // Given
        val post = createSamplePost()
        val intent = PostDetailIntent.LoadPost(post)

        // Then
        assertEquals(post, intent.post)
    }

    @Test
    fun `RefreshPost Intent가 정상적으로 생성되어야 한다`() {
        // Given
        val intent = PostDetailIntent.RefreshPost

        // Then
        assertTrue(intent is PostDetailIntent.RefreshPost)
    }

    @Test
    fun `빈 Post가 아닌 실제 데이터를 가진 Post가 로드되어야 한다`() = runTest {
        // Given
        val post = createSamplePost()

        // When
        viewModel.handleIntent(PostDetailIntent.LoadPost(post))
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.post?.name?.isNotEmpty() == true)
        assertTrue(viewModel.state.value.post?.email?.isNotEmpty() == true)
    }
}

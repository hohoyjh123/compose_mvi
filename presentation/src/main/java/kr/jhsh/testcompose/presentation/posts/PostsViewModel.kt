package kr.jhsh.testcompose.presentation.posts

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.usecase.GetPostsUseCase
import kr.jhsh.testcompose.presentation.base.BaseViewModel
import javax.inject.Inject

/**
 * [Hilt DI] PostsViewModel - Presentation Layer
 * - @HiltViewModel: Hilt가 ViewModel 의존성을 자동 주입
 * - 생성자 주입을 통해 GetPostsUseCase 주입
 * - Jetpack Paging 3 지원: posts Flow를 직접 UI에 노출
 * 
 * Note: PagingData는 snapshot 기반이므로 State에 저장할 수 없습니다.
 *       대신 Flow를 직접 노출하고 UI에서 collectAsLazyPagingItems()로 수집합니다.
 */
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : BaseViewModel<PostsState, PostsIntent, PostsEffect>(PostsState()) {

    /**
     * PagingData Flow - UI에서 collectAsLazyPagingItems()로 수집
     * cachedIn으로 viewModelScope에 캐싱하여 설정 변경 시에도 데이터 유지
     */
    val posts: Flow<PagingData<Post>> = getPostsUseCase()
        .cachedIn(viewModelScope)

    init {
        handleIntent(PostsIntent.LoadPosts)
    }

    override fun handleIntent(intent: PostsIntent) {
        when (intent) {
            is PostsIntent.LoadPosts -> loadPosts()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            setState { copy(isInitialLoading = true, error = null) }
            
            // Paging은 Flow로 자동 로드되므로 초기 로딩 상태만 관리
            // 실제 데이터는 posts Flow에서 collectAsLazyPagingItems()로 수집
            setState { copy(isInitialLoading = false) }
        }
    }
}

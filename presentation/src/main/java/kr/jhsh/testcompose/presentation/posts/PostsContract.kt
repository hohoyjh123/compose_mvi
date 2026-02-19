package kr.jhsh.testcompose.presentation.posts

import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.presentation.base.UiEffect
import kr.jhsh.testcompose.presentation.base.UiIntent
import kr.jhsh.testcompose.presentation.base.UiState

/**
 * [모듈 간 통신] Presentation 모듈이 Domain 모듈의 Post 모델 사용
 * - domain.Post를 UI 상태로 직접 사용
 * - domain 모듈에 대한 의존성을 통해 접근 가능
 */
data class PostsState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String? = null
) : UiState

sealed class PostsIntent : UiIntent {
    data object LoadPosts : PostsIntent()
    data object RefreshPosts : PostsIntent()
}

sealed class PostsEffect : UiEffect {
    data class ShowToast(val message: String) : PostsEffect()
}

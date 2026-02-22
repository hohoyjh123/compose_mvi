package kr.jhsh.testcompose.presentation.posts

import kr.jhsh.testcompose.presentation.base.UiEffect
import kr.jhsh.testcompose.presentation.base.UiIntent
import kr.jhsh.testcompose.presentation.base.UiState

/**
 * [모듈 간 통신] Presentation 모듈이 Domain 모듈의 Post 모델 사용
 * - domain.Post를 UI 상태로 직접 사용
 * - domain 모듈에 대한 의존성을 통해 접근 가능
 * - Jetpack Paging 3 지원
 * 
 * Note: PagingData는 State에 저장하지 않고 ViewModel에서 직접 Flow로 제공합니다.
 *       PagingData는 snapshot 기반이므로 State에 저장할 수 없습니다.
 */
data class PostsState(
    val isInitialLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class PostsIntent : UiIntent {
    data object LoadPosts : PostsIntent()
}

sealed class PostsEffect : UiEffect {
    data class ShowToast(val message: String) : PostsEffect()
}

package kr.jhsh.testcompose.presentation.postdetail

import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.presentation.base.UiEffect
import kr.jhsh.testcompose.presentation.base.UiIntent
import kr.jhsh.testcompose.presentation.base.UiState

/**
 * Post 상세 페이지 Contract
 * - 사용자 프로필 상세 정보 표시
 */
data class PostDetailState(
    val post: Post? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : UiState

sealed class PostDetailIntent : UiIntent {
    data class LoadPost(val post: Post) : PostDetailIntent()
    data object RefreshPost : PostDetailIntent()
}

sealed class PostDetailEffect : UiEffect {
    data class ShowToast(val message: String) : PostDetailEffect()
    data object NavigateBack : PostDetailEffect()
}
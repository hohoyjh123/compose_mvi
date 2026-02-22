package kr.jhsh.testcompose.presentation.postdetail

import dagger.hilt.android.lifecycle.HiltViewModel
import kr.jhsh.testcompose.presentation.base.BaseViewModel
import javax.inject.Inject

/**
 * Post 상세 페이지 ViewModel
 * - 목록에서 전달받은 Post 데이터를 표시
 * - API 호출 없이 전달받은 데이터 사용
 */
@HiltViewModel
class PostDetailViewModel @Inject constructor(
) : BaseViewModel<PostDetailState, PostDetailIntent, PostDetailEffect>(PostDetailState()) {

    override fun handleIntent(intent: PostDetailIntent) {
        when (intent) {
            is PostDetailIntent.LoadPost -> loadPost(intent.post)
            is PostDetailIntent.RefreshPost -> refreshPost()
        }
    }

    private fun loadPost(post: kr.jhsh.testcompose.domain.model.Post) {
        // API 호출 없이 전달받은 Post 데이터를 바로 사용
        setState { copy(isLoading = false, post = post, error = null) }
    }

    private fun refreshPost() {
        // Refresh logic if needed
    }
}

package kr.jhsh.testcompose.presentation.posts

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.jhsh.testcompose.domain.usecase.GetPostsUseCase
import kr.jhsh.testcompose.presentation.base.BaseViewModel
import javax.inject.Inject

/**
 * [Hilt DI] PostsViewModel - Presentation Layer
 * - @HiltViewModel: Hilt가 ViewModel 의존성을 자동 주입
 * - 생성자 주입을 통해 GetPostsUseCase 주입
 */
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : BaseViewModel<PostsState, PostsIntent, PostsEffect>(PostsState()) {

    init {
        handleIntent(PostsIntent.LoadPosts)
    }

    override fun handleIntent(intent: PostsIntent) {
        when (intent) {
            is PostsIntent.LoadPosts -> loadPosts()
            is PostsIntent.RefreshPosts -> loadPosts(isRefresh = true)
        }
    }

    private fun loadPosts(isRefresh: Boolean = false) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            getPostsUseCase()
                .onSuccess { posts ->
                    setState { copy(isLoading = false, posts = posts) }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                    sendEffect(PostsEffect.ShowToast("Error: ${throwable.message}"))
                }
        }
    }
}

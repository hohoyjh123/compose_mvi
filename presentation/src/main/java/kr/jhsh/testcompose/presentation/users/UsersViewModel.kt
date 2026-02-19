package kr.jhsh.testcompose.presentation.users

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.jhsh.testcompose.domain.usecase.GetUsersUseCase
import kr.jhsh.testcompose.presentation.base.BaseViewModel
import javax.inject.Inject

/**
 * [Hilt DI] UsersViewModel - Presentation Layer
 * - @HiltViewModel: Hilt가 ViewModel 의존성을 자동 주입
 * - 생성자 주입을 통해 GetUsersUseCase 주입
 */
@HiltViewModel
class UsersViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : BaseViewModel<UsersState, UsersIntent, UsersEffect>(UsersState()) {

    init {
        handleIntent(UsersIntent.LoadUsers)
    }

    override fun handleIntent(intent: UsersIntent) {
        when (intent) {
            is UsersIntent.LoadUsers -> loadUsers()
            is UsersIntent.RefreshUsers -> loadUsers(isRefresh = true)
        }
    }

    private fun loadUsers(isRefresh: Boolean = false) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            getUsersUseCase()
                .onSuccess { users ->
                    setState { copy(isLoading = false, users = users) }
                }
                .onFailure { throwable ->
                    setState { copy(isLoading = false, error = throwable.message) }
                    sendEffect(UsersEffect.ShowToast("Error: ${throwable.message}"))
                }
        }
    }
}

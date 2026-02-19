package kr.jhsh.testcompose.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.jhsh.testcompose.domain.usecase.GetUsersUseCase
import kr.jhsh.testcompose.presentation.base.BaseViewModel

/**
 * [모듈 간 통신] Presentation ViewModel이 Domain UseCase 사용
 * - GetUsersUseCase는 domain 모듈에서 제공
 * - app 모듈에서 Repository를 생성하고 UseCase를 만들어 주입해야 함
 * - [통신 방향]: ViewModel -> domain.UseCase -> domain.Repository 인터페이스
 */
class UsersViewModel(
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

            /**
             * [모듈 간 통신] UseCase 실행
             * - domain.GetUsersUseCase를 통해 domain.UserRepository 호출
             * - 실제 구현체는 data 모듈에 있으나, presentation은 domain 인터페이스만 의존
             */
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

    /**
     * [중요] ViewModel Factory
     * - app 모듈에서 UseCase를 주입받아 ViewModel 생성
     * - DI 프레임워크 사용 시 더 깔끔하게 처리 가능
     */
    class Factory(
        private val getUsersUseCase: GetUsersUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsersViewModel(getUsersUseCase) as T
        }
    }
}

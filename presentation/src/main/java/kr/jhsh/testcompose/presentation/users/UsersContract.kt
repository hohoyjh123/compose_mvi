package kr.jhsh.testcompose.presentation.users

import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.presentation.base.UiEffect
import kr.jhsh.testcompose.presentation.base.UiIntent
import kr.jhsh.testcompose.presentation.base.UiState

/**
 * [모듈 간 통신] Presentation 모듈이 Domain 모듈의 User 모델 사용
 * - domain.User를 UI 상태로 직접 사용
 * - domain 모듈에 대한 의존성을 통해 접근 가능
 */
data class UsersState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
) : UiState

sealed class UsersIntent : UiIntent {
    data object LoadUsers : UsersIntent()
    data object RefreshUsers : UsersIntent()
}

sealed class UsersEffect : UiEffect {
    data class ShowToast(val message: String) : UsersEffect()
}

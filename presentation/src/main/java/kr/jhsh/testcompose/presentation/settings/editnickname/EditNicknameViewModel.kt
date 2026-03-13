package kr.jhsh.testcompose.presentation.settings.editnickname

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kr.jhsh.testcompose.domain.usecase.GetNicknameUseCase
import kr.jhsh.testcompose.domain.usecase.SaveNicknameUseCase
import kr.jhsh.testcompose.presentation.base.BaseViewModel
import kr.jhsh.testcompose.presentation.base.UiEffect
import kr.jhsh.testcompose.presentation.base.UiIntent
import kr.jhsh.testcompose.presentation.base.UiState
import javax.inject.Inject

/**
 * 닉네임 편집 화면의 상태
 */
data class EditNicknameState(
    val currentNickname: String = "",
    val newNickname: String = "",
    val validationMessage: String = "",
    val isValidationSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
) : UiState

/**
 * 닉네임 편집 화면의 사용자 의도
 */
sealed class EditNicknameIntent : UiIntent {
    data class OnNewNicknameChanged(val nickname: String) : EditNicknameIntent()
    data object OnClearNickname : EditNicknameIntent()
    data object OnSaveNickname : EditNicknameIntent()
    data object OnNavigateBack : EditNicknameIntent()
}

/**
 * 닉네임 편집 화면의 Side Effect
 */
sealed class EditNicknameEffect : UiEffect {
    data object NavigateBack : EditNicknameEffect()
    data class ShowToast(val message: String) : EditNicknameEffect()
}

/**
 * 닉네임 편집 ViewModel
 */
@HiltViewModel
class EditNicknameViewModel @Inject constructor(
    private val getNicknameUseCase: GetNicknameUseCase,
    private val saveNicknameUseCase: SaveNicknameUseCase
) : BaseViewModel<EditNicknameState, EditNicknameIntent, EditNicknameEffect>(
    initialState = EditNicknameState()
) {

    companion object {
        // 닉네임 유효성 검사 정규식: 4~12자, 영문자와 숫자만 사용, 공백 사용 불가
        private val NICKNAME_REGEX = Regex("^[a-zA-Z0-9]{4,12}$")
        private const val MIN_LENGTH = 4
        private const val MAX_LENGTH = 12
    }

    init {
        loadCurrentNickname()
    }

    override fun handleIntent(intent: EditNicknameIntent) {
        when (intent) {
            is EditNicknameIntent.OnNewNicknameChanged -> {
                validateAndUpdateNickname(intent.nickname)
            }

            is EditNicknameIntent.OnClearNickname -> {
                setState {
                    copy(
                        newNickname = "",
                        validationMessage = "",
                        isValidationSuccess = false
                    )
                }
            }

            is EditNicknameIntent.OnSaveNickname -> {
                saveNickname()
            }

            is EditNicknameIntent.OnNavigateBack -> {
                sendEffect(EditNicknameEffect.NavigateBack)
            }
        }
    }

    private fun loadCurrentNickname() {
        getNicknameUseCase()
            .onEach { nickname ->
                setState {
                    copy(
                        currentNickname = nickname ?: "",
                        newNickname = ""
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun validateAndUpdateNickname(nickname: String) {
        val validationResult = validateNickname(nickname)
        setState {
            copy(
                newNickname = nickname,
                validationMessage = validationResult.message,
                isValidationSuccess = validationResult.isSuccess
            )
        }
    }

    private fun validateNickname(nickname: String): ValidationResult {
        return when {
            nickname.isEmpty() -> ValidationResult(
                isSuccess = false,
                message = "닉네임을 입력해주세요"
            )

            nickname.contains(" ") -> ValidationResult(
                isSuccess = false,
                message = "닉네임에 공백을 사용할 수 없습니다"
            )

            nickname.length < MIN_LENGTH -> ValidationResult(
                isSuccess = false,
                message = "닉네임은 ${MIN_LENGTH}자 이상이어야 합니다"
            )

            nickname.length > MAX_LENGTH -> ValidationResult(
                isSuccess = false,
                message = "닉네임은 ${MAX_LENGTH}자 이하여야 합니다"
            )

            !NICKNAME_REGEX.matches(nickname) -> ValidationResult(
                isSuccess = false,
                message = "닉네임은 영문자와 숫자만 사용 가능합니다"
            )

            else -> ValidationResult(
                isSuccess = true,
                message = "사용 가능한 닉네임입니다"
            )
        }
    }

    private fun saveNickname() {
        val newNickname = state.value.newNickname
        val validationResult = validateNickname(newNickname)

        if (!validationResult.isSuccess) {
            setState {
                copy(
                    validationMessage = validationResult.message,
                    isValidationSuccess = false
                )
            }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                saveNicknameUseCase(newNickname)
                setState {
                    copy(
                        isLoading = false,
                        isSaved = true,
                        currentNickname = newNickname,
                        newNickname = ""
                    )
                }
                sendEffect(EditNicknameEffect.ShowToast("닉네임이 저장되었습니다"))
                sendEffect(EditNicknameEffect.NavigateBack)
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                sendEffect(EditNicknameEffect.ShowToast("저장 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    private data class ValidationResult(
        val isSuccess: Boolean,
        val message: String
    )
}

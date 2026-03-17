package kr.jhsh.testcompose.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kr.jhsh.testcompose.domain.usecase.GetNicknameUseCase
import javax.inject.Inject

/**
 * Settings 화면의 ViewModel
 * - 닉네임 정보를 관리
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    getNicknameUseCase: GetNicknameUseCase
) : ViewModel() {

    /**
     * 저장된 닉네임 상태
     */
    val nickname: StateFlow<String?> = getNicknameUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
}

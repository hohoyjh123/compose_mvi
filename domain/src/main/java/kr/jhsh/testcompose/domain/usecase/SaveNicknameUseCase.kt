package kr.jhsh.testcompose.domain.usecase

import kr.jhsh.testcompose.domain.repository.NicknameRepository
import javax.inject.Inject

/**
 * 닉네임을 저장하는 UseCase
 */
class SaveNicknameUseCase @Inject constructor(
    private val nicknameRepository: NicknameRepository
) {
    suspend operator fun invoke(nickname: String) {
        nicknameRepository.saveNickname(nickname)
    }
}

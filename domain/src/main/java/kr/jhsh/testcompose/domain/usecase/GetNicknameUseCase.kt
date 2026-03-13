package kr.jhsh.testcompose.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.jhsh.testcompose.domain.repository.NicknameRepository
import javax.inject.Inject

/**
 * 저장된 닉네임을 가져오는 UseCase
 */
class GetNicknameUseCase @Inject constructor(
    private val nicknameRepository: NicknameRepository
) {
    operator fun invoke(): Flow<String?> {
        return nicknameRepository.getNickname()
    }
}

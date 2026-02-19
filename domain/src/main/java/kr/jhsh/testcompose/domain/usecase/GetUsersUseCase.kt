package kr.jhsh.testcompose.domain.usecase

import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.domain.repository.UserRepository
import javax.inject.Inject

/**
 * [Hilt DI] GetUsersUseCase (Domain 모듈)
 * - 생성자 주입을 통해 UserRepository 의존성 주입
 * - presentation 모듈: ViewModel에서 이 UseCase를 주입받아 사용
 */

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getUsers()
    }
}

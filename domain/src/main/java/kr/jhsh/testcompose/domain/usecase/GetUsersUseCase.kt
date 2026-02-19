package kr.jhsh.testcompose.domain.usecase

import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.domain.repository.UserRepository

/**
 * [모듈 간 통신] UseCase (Domain 모듈)
 * - presentation 모듈: ViewModel에서 이 UseCase를 주입받아 사용
 * - 내부적으로 domain 모듈의 UserRepository 인터페이스에 의존
 */
class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return repository.getUsers()
    }
}

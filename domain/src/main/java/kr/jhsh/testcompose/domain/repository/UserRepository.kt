package kr.jhsh.testcompose.domain.repository

import kr.jhsh.testcompose.domain.model.User

/**
 * [모듈 간 통신] Repository 인터페이스 (Domain 모듈)
 * - data 모듈: 이 인터페이스를 구현하여 실제 데이터 로직 제공
 * - presentation 모듈: UseCase를 통해 이 인터페이스에 의존 (DIP 원칙)
 */
interface UserRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun getUserById(id: Int): Result<User>
}

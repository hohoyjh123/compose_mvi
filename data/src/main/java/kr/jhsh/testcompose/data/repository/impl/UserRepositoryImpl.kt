package kr.jhsh.testcompose.data.repository.impl

import kr.jhsh.testcompose.data.mapper.toDomain
import kr.jhsh.testcompose.data.remote.api.JsonPlaceholderApi
import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.domain.repository.UserRepository

/**
 * [모듈 간 통신] Domain Repository 인터페이스 구현체
 * - data 모듈에 위치하며, domain.UserRepository 인터페이스를 구현
 * - [통신 방향]: presentation -> domain 인터페이스 -> data 구현체
 * - domain 모델을 반환하여 presentation이 직접 사용 가능
 */
class UserRepositoryImpl(
    private val api: JsonPlaceholderApi
) : UserRepository {

    override suspend fun getUsers(): Result<List<User>> = runCatching {
        api.getUsers().map { it.toDomain() }
    }

    override suspend fun getUserById(id: Int): Result<User> = runCatching {
        api.getUserById(id).toDomain()
    }
}

package kr.jhsh.testcompose.data.repository.impl

import kr.jhsh.testcompose.data.mapper.toDomain
import kr.jhsh.testcompose.data.remote.api.JsonPlaceholderApi
import kr.jhsh.testcompose.domain.model.User
import kr.jhsh.testcompose.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Hilt DI] UserRepository 인터페이스 구현체
 * - data 모듈에 위치하며, domain.UserRepository 인터페이스를 구현
 * - 생성자 주입을 통해 JsonPlaceholderApi 의존성 주입
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: JsonPlaceholderApi
) : UserRepository {

    override suspend fun getUsers(): Result<List<User>> = runCatching {
        api.getUsers().map { it.toDomain() }
    }

    override suspend fun getUserById(id: Int): Result<User> = runCatching {
        api.getUserById(id).toDomain()
    }
}

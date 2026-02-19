package kr.jhsh.testcompose.data.repository.impl

import kr.jhsh.testcompose.data.mapper.toDomain
import kr.jhsh.testcompose.data.remote.api.JsonPlaceholderApi
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Hilt DI] PostRepository 인터페이스 구현체
 * - data 모듈에 위치하며, domain.PostRepository 인터페이스를 구현
 * - 생성자 주입을 통해 JsonPlaceholderApi 의존성 주입
 */
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val api: JsonPlaceholderApi
) : PostRepository {

    override suspend fun getPosts(): Result<List<Post>> = runCatching {
        api.getPosts().map { it.toDomain() }
    }

    override suspend fun getPostById(id: Int): Result<Post> = runCatching {
        api.getPostById(id).toDomain()
    }
}

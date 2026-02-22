package kr.jhsh.testcompose.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.jhsh.testcompose.data.mapper.toDomain
import kr.jhsh.testcompose.data.remote.api.RandomUserApi
import kr.jhsh.testcompose.data.repository.RandomUserPagingSource
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [Hilt DI] PostRepository 인터페이스 구현체 (RandomUser API)
 * - data 모듈에 위치하며, domain.PostRepository 인터페이스를 구현
 * - Jetpack Paging 3을 사용하여 페이지네이션 지원
 * - 생성자 주입을 통해 RandomUserApi 의존성 주입
 */
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val api: RandomUserApi
) : PostRepository {

    override fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = RandomUserApi.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { RandomUserPagingSource(api) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }
}

package kr.jhsh.testcompose.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.repository.PostRepository
import javax.inject.Inject

/**
 * [Hilt DI] GetPostsUseCase (Domain 모듈)
 * - 생성자 주입을 통해 PostRepository 의존성 주입
 * - presentation 모듈: ViewModel에서 이 UseCase를 주입받아 사용
 * - Jetpack Paging 3 지원: Flow<PagingData<Post>> 반환
 */
class GetPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> {
        return repository.getPosts()
    }
}

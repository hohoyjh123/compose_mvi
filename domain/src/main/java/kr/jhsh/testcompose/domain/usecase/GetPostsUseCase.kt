package kr.jhsh.testcompose.domain.usecase

import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.repository.PostRepository

/**
 * [모듈 간 통신] UseCase (Domain 모듈)
 * - presentation 모듈: ViewModel에서 이 UseCase를 주입받아 사용
 * - 내부적으로 domain 모듈의 PostRepository 인터페이스에 의존
 */
class GetPostsUseCase(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): Result<List<Post>> {
        return repository.getPosts()
    }
}

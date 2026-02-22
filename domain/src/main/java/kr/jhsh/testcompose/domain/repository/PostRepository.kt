package kr.jhsh.testcompose.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kr.jhsh.testcompose.domain.model.Post

/**
 * [모듈 간 통신] Repository 인터페이스 (Domain 모듈)
 * - data 모듈: 이 인터페이스를 구현하여 실제 데이터 로직 제공
 * - presentation 모듈: UseCase를 통해 이 인터페이스에 의존 (DIP 원칙)
 * - Jetpack Paging 3 지원
 */
interface PostRepository {
    fun getPosts(): Flow<PagingData<Post>>
}

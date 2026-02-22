package kr.jhsh.testcompose.data.remote.api

import kr.jhsh.testcompose.data.remote.dto.RandomUserResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * RandomUser.me API 인터페이스
 * - 페이지네이션 지원: page, results 파라미터 사용
 * - seed 파라미터로 일관된 데이터 제공
 */
interface RandomUserApi {

    @GET("api/")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("results") results: Int = PAGE_SIZE,
        @Query("seed") seed: String = "abc"
    ): RandomUserResponseDto

    companion object {
        const val BASE_URL = "https://randomuser.me/"
        const val PAGE_SIZE = 20
    }
}

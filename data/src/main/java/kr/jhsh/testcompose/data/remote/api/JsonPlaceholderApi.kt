package kr.jhsh.testcompose.data.remote.api

import kr.jhsh.testcompose.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * JsonPlaceholder API 인터페이스 (User 탭 전용)
 * - BASE_URL: https://jsonplaceholder.typicode.com/
 * - User 탭에서 사용자 정보 조회용으로 사용
 */
interface JsonPlaceholderApi {

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}

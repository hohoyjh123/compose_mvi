package kr.jhsh.testcompose.data.remote.api

import kr.jhsh.testcompose.data.remote.dto.PostDto
import kr.jhsh.testcompose.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Data 모듈 내부 Retrofit API 인터페이스
 * - [모듈 간 통신] data 모듈 내부에서만 사용
 * - 외부 모듈(presentation)은 이 인터페이스를 직접 접근하지 않음
 */
interface JsonPlaceholderApi {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): PostDto

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    }
}

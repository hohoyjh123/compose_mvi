package kr.jhsh.testcompose.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kr.jhsh.testcompose.data.remote.api.JsonPlaceholderApi
import kr.jhsh.testcompose.data.repository.impl.PostRepositoryImpl
import kr.jhsh.testcompose.data.repository.impl.UserRepositoryImpl
import kr.jhsh.testcompose.domain.repository.PostRepository
import kr.jhsh.testcompose.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * [모듈 간 통신] Data 모듈의 DI 컨테이너
 * - data 모듈 내부에서 네트워크와 Repository 인스턴스를 생성
 * - [중요]: domain 모듈의 Repository 인터페이스 구현체를 제공
 * - app 모듈에서 이 DI를 통해 Repository를 얻어 ViewModel에 주입
 */
object AppModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(JsonPlaceholderApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    private val api: JsonPlaceholderApi by lazy {
        retrofit.create(JsonPlaceholderApi::class.java)
    }

    /**
     * [모듈 간 통신] PostRepository 제공
     * - 반환 타입: domain.PostRepository (인터페이스)
     * - 실제 구현: data.PostRepositoryImpl
     * - app/presentation 모듈에서 사용
     */
    val postRepository: PostRepository by lazy {
        PostRepositoryImpl(api)
    }

    /**
     * [모듈 간 통신] UserRepository 제공
     * - 반환 타입: domain.UserRepository (인터페이스)
     * - 실제 구현: data.UserRepositoryImpl
     * - app/presentation 모듈에서 사용
     */
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(api)
    }
}

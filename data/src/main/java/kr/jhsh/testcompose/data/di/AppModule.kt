package kr.jhsh.testcompose.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kr.jhsh.testcompose.data.remote.api.JsonPlaceholderApi
import kr.jhsh.testcompose.data.remote.api.RandomUserApi
import kr.jhsh.testcompose.data.repository.impl.PostRepositoryImpl
import kr.jhsh.testcompose.data.repository.impl.UserRepositoryImpl
import kr.jhsh.testcompose.domain.repository.PostRepository
import kr.jhsh.testcompose.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for RandomUser.me API Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RandomUser

/**
 * Qualifier for JsonPlaceholder API Retrofit
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class JsonPlaceholder

/**
 * Hilt DI Module - Data Layer
 * - Network (Retrofit, OkHttp) 제공
 * - Repository 구현체 제공
 * - Post 탭: RandomUser.me API 사용
 * - User 탭: JsonPlaceholder API 사용
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * RandomUser.me API용 Retrofit (Post 탭)
     * BASE_URL: https://randomuser.me/
     */
    @RandomUser
    @Provides
    @Singleton
    fun provideRandomUserRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(RandomUserApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    /**
     * JsonPlaceholder API용 Retrofit (User 탭)
     * BASE_URL: https://jsonplaceholder.typicode.com/
     */
    @JsonPlaceholder
    @Provides
    @Singleton
    fun provideJsonPlaceholderRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(JsonPlaceholderApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideRandomUserApi(@RandomUser retrofit: Retrofit): RandomUserApi {
        return retrofit.create(RandomUserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideJsonPlaceholderApi(
        @JsonPlaceholder retrofit: Retrofit
    ): JsonPlaceholderApi {
        return retrofit.create(JsonPlaceholderApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostRepository(api: RandomUserApi): PostRepository {
        return PostRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: JsonPlaceholderApi): UserRepository {
        return UserRepositoryImpl(api)
    }
}

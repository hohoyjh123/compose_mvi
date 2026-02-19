package kr.jhsh.testcompose.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Data 모듈 내부 네트워크 인터셉터
 * - [모듈 간 통신] data 모듈 내부에서만 사용
 * - OkHttpClient 설정에 포함되어 로깅, 인증 등 처리
 */
class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        return response
    }
}

package kr.jhsh.testcompose.domain.model

import kotlinx.serialization.Serializable

/**
 * [모듈 간 통신] Domain 모듈의 핵심 데이터 클래스
 * - data 모듈: DTO를 이 모델로 매핑하여 사용 (RandomUser API)
 * - presentation 모듈: UI에서 이 모델을 직접 사용
 * - RandomUser.me API 응답 데이터 구조에 맞춰 정의
 */
@Serializable
data class Post(
    val id: String,           // UUID from login.uuid
    val name: String,         // Full name (title + first + last)
    val email: String,        // Email address
    val phone: String,        // Phone number
    val cell: String,         // Cell phone number
    val gender: String,       // Gender
    val age: Int,             // Age from dob
    val country: String,      // Country from location
    val city: String,         // City from location
    val pictureThumbnail: String,  // Thumbnail image URL
    val pictureLarge: String,      // Large image URL
    val nationality: String     // Nationality code
)

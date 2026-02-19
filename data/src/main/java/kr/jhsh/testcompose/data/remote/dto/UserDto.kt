package kr.jhsh.testcompose.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data 모듈 내부 DTO
 * - 원격 API 응답을 매핑하는 전용 클래스
 * - [모듈 간 통신] Data -> Domain: Mapper를 통해 domain.model.User로 변환됨
 */
@Serializable
data class UserDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("website") val website: String? = null
)

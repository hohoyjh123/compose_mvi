package kr.jhsh.testcompose.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data 모듈 내부 DTO
 * - 원격 API 응답을 매핑하는 전용 클래스
 * - [모듈 간 통신] Data -> Domain: Mapper를 통해 domain.model.Post로 변환됨
 */
@Serializable
data class PostDto(
    @SerialName("id") val id: Int,
    @SerialName("userId") val userId: Int,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String
)

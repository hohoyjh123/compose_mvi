package kr.jhsh.testcompose.domain.model

/**
 * [모듈 간 통신] Domain 모듈의 핵심 데이터 클래스
 * - data 모듈: DTO를 이 모델로 매핑하여 사용
 * - presentation 모듈: UI에서 이 모델을 직접 사용
 */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    val website: String? = null
)

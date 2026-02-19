package kr.jhsh.testcompose.data.mapper

import kr.jhsh.testcompose.data.remote.dto.PostDto
import kr.jhsh.testcompose.data.remote.dto.UserDto
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.model.User

/**
 * [모듈 간 통신] Data -> Domain 매퍼
 * - data 모듈의 DTO를 domain 모듈의 Model로 변환
 * - data 모듈이 domain 모듈에 의존하여 이 변환이 가능
 * - Repository 구현체에서 사용됨
 */
fun PostDto.toDomain(): Post = Post(
    id = id,
    userId = userId,
    title = title,
    body = body
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)

package kr.jhsh.testcompose.data.mapper

import kr.jhsh.testcompose.data.remote.dto.RandomUserDto
import kr.jhsh.testcompose.data.remote.dto.UserDto
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.domain.model.User

/**
 * [모듈 간 통신] Data -> Domain 매퍼
 * - data 모듈의 DTO를 domain 모듈의 Model로 변환
 * - data 모듈이 domain 모듈에 의존하여 이 변환이 가능
 * - Repository 구현체에서 사용됨
 */

/**
 * RandomUserDto -> Post (Domain Model)
 * - RandomUser.me API 응답을 UI에 표시할 모델로 변환
 */
fun RandomUserDto.toDomain(): Post = Post(
    id = login.uuid,
    name = "${name.title} ${name.first} ${name.last}",
    email = email,
    phone = phone,
    cell = cell,
    gender = gender,
    age = dob.age,
    country = location.country,
    city = location.city,
    pictureThumbnail = picture.thumbnail,
    pictureLarge = picture.large,
    nationality = nationality
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website
)

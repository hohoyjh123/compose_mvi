package kr.jhsh.testcompose.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * RandomUser.me API 응답 DTO
 * - API: https://randomuser.me/api/?page=1&results=20&seed=abc
 */
@Serializable
data class RandomUserResponseDto(
    @SerialName("results") val results: List<RandomUserDto>,
    @SerialName("info") val info: RandomUserInfoDto
)

@Serializable
data class RandomUserDto(
    @SerialName("gender") val gender: String,
    @SerialName("name") val name: RandomUserNameDto,
    @SerialName("location") val location: RandomUserLocationDto,
    @SerialName("email") val email: String,
    @SerialName("login") val login: RandomUserLoginDto,
    @SerialName("dob") val dob: RandomUserDateDto,
    @SerialName("phone") val phone: String,
    @SerialName("cell") val cell: String,
    @SerialName("id") val id: RandomUserIdDto?,
    @SerialName("picture") val picture: RandomUserPictureDto,
    @SerialName("nat") val nationality: String
)

@Serializable
data class RandomUserNameDto(
    @SerialName("title") val title: String,
    @SerialName("first") val first: String,
    @SerialName("last") val last: String
)

@Serializable
data class RandomUserLocationDto(
    @SerialName("street") val street: RandomUserStreetDto,
    @SerialName("city") val city: String,
    @SerialName("state") val state: String,
    @SerialName("country") val country: String
)

@Serializable
data class RandomUserStreetDto(
    @SerialName("number") val number: Int,
    @SerialName("name") val name: String
)

@Serializable
data class RandomUserLoginDto(
    @SerialName("uuid") val uuid: String,
    @SerialName("username") val username: String
)

@Serializable
data class RandomUserDateDto(
    @SerialName("date") val date: String,
    @SerialName("age") val age: Int
)

@Serializable
data class RandomUserIdDto(
    @SerialName("name") val name: String? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class RandomUserPictureDto(
    @SerialName("large") val large: String,
    @SerialName("medium") val medium: String,
    @SerialName("thumbnail") val thumbnail: String
)

@Serializable
data class RandomUserInfoDto(
    @SerialName("seed") val seed: String,
    @SerialName("results") val results: Int,
    @SerialName("page") val page: Int,
    @SerialName("version") val version: String
)

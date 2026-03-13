package kr.jhsh.testcompose.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * 닉네임 저장소 인터페이스
 */
interface NicknameRepository {
    /**
     * 저장된 닉네임을 Flow로 가져오기
     */
    fun getNickname(): Flow<String?>

    /**
     * 닉네임 저장하기
     */
    suspend fun saveNickname(nickname: String)

    /**
     * 닉네임 삭제하기
     */
    suspend fun clearNickname()
}

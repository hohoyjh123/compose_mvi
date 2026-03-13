package kr.jhsh.testcompose.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kr.jhsh.testcompose.data.local.UserPreferencesDataStore
import kr.jhsh.testcompose.domain.repository.NicknameRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 닉네임 저장소 구현체
 */
@Singleton
class NicknameRepositoryImpl @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : NicknameRepository {

    override fun getNickname(): Flow<String?> {
        return userPreferencesDataStore.nickname
    }

    override suspend fun saveNickname(nickname: String) {
        userPreferencesDataStore.saveNickname(nickname)
    }

    override suspend fun clearNickname() {
        userPreferencesDataStore.clearNickname()
    }
}

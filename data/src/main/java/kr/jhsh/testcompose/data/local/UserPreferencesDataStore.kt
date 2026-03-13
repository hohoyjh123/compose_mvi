package kr.jhsh.testcompose.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * 사용자 설정을 로컬에 저장하는 DataStore 관리 클래스
 */
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val NICKNAME_KEY = stringPreferencesKey("nickname")
    }

    /**
     * 닉네임 Flow로 가져오기
     */
    val nickname: Flow<String?> = context.userDataStore.data
        .map { preferences ->
            preferences[NICKNAME_KEY]
        }

    /**
     * 닉네임 저장하기
     */
    suspend fun saveNickname(nickname: String) {
        context.userDataStore.edit { preferences ->
            preferences[NICKNAME_KEY] = nickname
        }
    }

    /**
     * 닉네임 삭제하기 (초기화)
     */
    suspend fun clearNickname() {
        context.userDataStore.edit { preferences ->
            preferences.remove(NICKNAME_KEY)
        }
    }
}

package kr.jhsh.testcompose.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import kr.jhsh.testcompose.domain.model.Post

/**
 * Post 객체를 Navigation 인자로 전달하기 위한 NavType
 * - kotlinx.serialization을 사용하여 객체를 JSON 문자열로 직렬화/역직렬화
 */
val PostType = object : NavType<Post>(
    isNullableAllowed = false
) {
    override fun get(bundle: Bundle, key: String): Post? {
        return bundle.getString(key)?.let { parseValue(it) }
    }

    override fun parseValue(value: String): Post {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: Post): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: Post) {
        bundle.putString(key, serializeAsValue(value))
    }
}

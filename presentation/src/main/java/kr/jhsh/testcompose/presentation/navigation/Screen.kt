package kr.jhsh.testcompose.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Presentation 모듈 내부 네비게이션 라우트
 * - [모듈 간 통신] app 모듈에서 Navigation Host 구성에 사용
 * - Navigation Component의 type-safe navigation을 위한 sealed class
 */
sealed class Screen {
    @Serializable
    data object Posts : Screen()

    @Serializable
    data object Users : Screen()

    @Serializable
    data object Settings : Screen()
}

/**
 * 바텀 네비게이션 아이템 데이터 클래스
 * - app 모듈의 Navigation에서 BottomBar 구성에 사용
 */
data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Screen
)

/**
 * 앱의 바텀 네비게이션 아이템 목록
 * - [모듈 간 통신] app 모듈에서 MainNavigation을 구성할 때 사용됨
 */
val bottomNavItems = listOf(
    BottomNavItem(
        title = "Posts",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add,
        route = Screen.Posts
    ),
    BottomNavItem(
        title = "Users",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        route = Screen.Users
    ),
    BottomNavItem(
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        route = Screen.Settings
    )
)

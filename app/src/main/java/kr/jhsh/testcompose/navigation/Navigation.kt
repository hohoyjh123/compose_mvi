package kr.jhsh.testcompose.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kr.jhsh.testcompose.domain.model.Post
import kr.jhsh.testcompose.presentation.navigation.Screen
import kr.jhsh.testcompose.presentation.navigation.bottomNavItems
import kr.jhsh.testcompose.presentation.postdetail.PostDetailScreen
import kr.jhsh.testcompose.presentation.posts.PostsScreen
import kr.jhsh.testcompose.presentation.posts.PostsViewModel
import kr.jhsh.testcompose.presentation.settings.SettingsScreen
import kr.jhsh.testcompose.presentation.users.UsersScreen
import kr.jhsh.testcompose.presentation.users.UsersViewModel
import kotlin.reflect.typeOf

/**
 * [Hilt DI] 메인 네비게이션 - App 모듈에서 관리
 * - presentation 모듈의 Screen들을 호스팅
 * - hiltViewModel()을 통해 자동으로 ViewModel 주입
 * - [의존성 주입 흐름]:
 *   1. AppModule (data) -> Network/Repository 제공
 *   2. Repository -> UseCase 주입 (domain)
 *   3. UseCase -> ViewModel 주입 (presentation via Hilt)
 *   4. ViewModel -> UI에 state/effect 제공
 */
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val postTypeMap = mapOf(
        typeOf<Post>() to PostType
    )

    // 현재 route 감지
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // bottomBar를 보여줄 화면 정의
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute(Screen.Posts::class) ||
                it.hasRoute(Screen.Users::class) ||
                it.hasRoute(Screen.Settings::class)
    } == true

    SharedTransitionLayout {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {   // 여기만 조건 추가
                    NavigationBar {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.hasRoute(item.route::class)
                            } == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = { Text(item.title) },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHostContainer(
                navController = navController,
                paddingValues = innerPadding,
                postTypeMap = postTypeMap
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun NavHostContainer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    postTypeMap: Map<kotlin.reflect.KType, NavType<*>>
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Screen.Posts,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.Posts> {
                // [Hilt DI] hiltViewModel()로 자동 주입
                val viewModel: PostsViewModel = hiltViewModel()
                PostsScreen(
                    viewModel = viewModel,
                    onPostClick = { post: Post ->
                        navController.navigate(Screen.PostDetail(post))
                    },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
            composable<Screen.PostDetail>(
                typeMap = postTypeMap
            ) { backStackEntry ->
                val postDetail: Screen.PostDetail = backStackEntry.toRoute()
                PostDetailScreen(
                    post = postDetail.post,
                    onNavigateBack = { navController.popBackStack() },
                    animatedVisibilityScope = this@composable,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
            composable<Screen.Users> {
                // [Hilt DI] hiltViewModel()로 자동 주입
                val viewModel: UsersViewModel = hiltViewModel()
                UsersScreen(viewModel = viewModel)
            }
            composable<Screen.Settings> {
                SettingsScreen()
            }
        }
    }
}

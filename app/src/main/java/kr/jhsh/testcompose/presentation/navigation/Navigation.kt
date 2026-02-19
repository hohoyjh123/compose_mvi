package kr.jhsh.testcompose.presentation.navigation

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kr.jhsh.testcompose.data.di.AppModule
import kr.jhsh.testcompose.domain.usecase.GetPostsUseCase
import kr.jhsh.testcompose.domain.usecase.GetUsersUseCase
import kr.jhsh.testcompose.presentation.posts.PostsScreen
import kr.jhsh.testcompose.presentation.posts.PostsViewModel
import kr.jhsh.testcompose.presentation.settings.SettingsScreen
import kr.jhsh.testcompose.presentation.users.UsersScreen
import kr.jhsh.testcompose.presentation.users.UsersViewModel

/**
 * [모듈 간 통신] 메인 네비게이션 - App 모듈에서 관리
 * - presentation 모듈의 Screen들을 호스팅
 * - data 모듈의 DI를 통해 UseCase와 ViewModel Factory 생성
 * - [통신 흐름]:
 *   1. AppModule (data) -> Repository 생성
 *   2. Repository -> GetPostsUseCase/GetUsersUseCase 생성 (domain)
 *   3. UseCase -> ViewModel Factory에 주입 (presentation)
 *   4. ViewModel -> UI에 state/effect 제공 (presentation)
 */
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // [모듈 간 통신] Data 모듈의 DI를 통해 Repository 주입받아 UseCase 생성
    val getPostsUseCase = GetPostsUseCase(AppModule.postRepository)
    val getUsersUseCase = GetUsersUseCase(AppModule.userRepository)

    // [모듈 간 통신] UseCase를 ViewModel Factory에 주입
    val postsViewModelFactory = PostsViewModel.Factory(getPostsUseCase)
    val usersViewModelFactory = UsersViewModel.Factory(getUsersUseCase)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

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
    ) { innerPadding ->
        NavHostContainer(
            navController = navController,
            paddingValues = innerPadding,
            postsViewModelFactory = postsViewModelFactory,
            usersViewModelFactory = usersViewModelFactory
        )
    }
}

@Composable
private fun NavHostContainer(
    navController: NavHostController,
    paddingValues: PaddingValues,
    postsViewModelFactory: PostsViewModel.Factory,
    usersViewModelFactory: UsersViewModel.Factory
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Posts,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable<Screen.Posts> {
            // [모듈 간 통신] Presentation 모듈의 Screen에 ViewModel Factory 주입
            PostsScreen(
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable<Screen.Users> {
            // [모듈 간 통신] Presentation 모듈의 Screen에 ViewModel Factory 주입
            UsersScreen(
                viewModel = viewModel(factory = usersViewModelFactory)
            )
        }
        composable<Screen.Settings> {
            SettingsScreen()
        }
    }
}

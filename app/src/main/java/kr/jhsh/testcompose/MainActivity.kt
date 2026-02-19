package kr.jhsh.testcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import kr.jhsh.testcompose.presentation.navigation.MainNavigation
import kr.jhsh.testcompose.ui.theme.TestComposeTheme

/**
 * [Hilt DI] 메인 액티비티 - App 모듈
 * - @AndroidEntryPoint: Hilt가 Activity를 위한 의존성 주입 제공
 * - presentation 모듈의 MainNavigation 호출
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestComposeTheme {
                MainNavigation()
            }
        }
    }
}

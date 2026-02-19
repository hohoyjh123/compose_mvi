package kr.jhsh.testcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kr.jhsh.testcompose.presentation.navigation.MainNavigation
import kr.jhsh.testcompose.ui.theme.TestComposeTheme

/**
 * [모듈 간 통신] 메인 액티비티 - App 모듈
 * - presentation 모듈의 MainNavigation 호출
 * - app 모듈의 Theme 적용
 * - [의존성]: presentation (navigation/screens) + app (theme)
 */
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

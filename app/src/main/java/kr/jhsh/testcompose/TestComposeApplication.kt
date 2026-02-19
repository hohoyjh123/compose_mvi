package kr.jhsh.testcompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * [Hilt DI] Application 클래스 - App 모듈
 * - @HiltAndroidApp: Hilt DI 시스템의 진입점
 * - 앱 전역 의존성 주입을 위한 컨테이너 생성
 */
@HiltAndroidApp
class TestComposeApplication : Application()

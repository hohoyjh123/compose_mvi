package kr.jhsh.testcompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application 클래스 - App 모듈
 * - Multi-module Hilt 설정을 위한 진입점
 * - 실제 DI는 data 모듈의 AppModule에서 수동으로 처리 (간단한 예제용)
 */
@HiltAndroidApp
class TestComposeApplication : Application()

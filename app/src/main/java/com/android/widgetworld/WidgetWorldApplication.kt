package com.android.widgetworld

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * WidgetWorld Application Class
 * 
 * Hilt DI 설정을 위한 Application 클래스입니다.
 * PRD: MVVM + Clean Architecture, Hilt를 사용한 의존성 주입
 */
@HiltAndroidApp
class WidgetWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application 초기화 로직은 여기에 추가
    }
}


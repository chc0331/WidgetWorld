package com.android.widgetworld.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App Module
 * 
 * 애플리케이션 전역 의존성을 제공하는 Hilt 모듈입니다.
 * PRD: DI는 Hilt를 사용하며, 구조는 단순하게 유지 (MVP 중심)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 전역 의존성 제공 메서드는 여기에 추가
    // 예: @Provides fun provideContext(@ApplicationContext context: Context): Context
}


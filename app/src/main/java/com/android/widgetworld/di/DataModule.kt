package com.android.widgetworld.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Data Module
 * 
 * DataStore, Repository 등 데이터 계층 의존성을 제공하는 Hilt 모듈입니다.
 * 
 * 향후 추가될 내용:
 * - Proto DataStore 인스턴스 제공
 * - Repository 구현체 바인딩
 * 
 * PRD: Storage는 DataStore(Proto) 사용, 추후 Room으로 확장 가능
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    // DataStore, Repository 제공 메서드는 섹션 2-3에서 추가
}


package com.android.widgetworld.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.android.widgetworld.data.datasource.WidgetDataStoreSource
import com.android.widgetworld.data.datasource.WidgetDocumentLocalDataSource
import com.android.widgetworld.data.datastore.WidgetDataStore
import com.android.widgetworld.data.datastore.WidgetDocumentSerializer
import com.android.widgetworld.data.repository.WidgetRepositoryImpl
import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.WidgetDocument
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Data Module
 * 
 * DataStore, Repository 등 데이터 계층 의존성을 제공하는 Hilt 모듈입니다.
 * 
 * 제공하는 의존성:
 * 1. Proto DataStore<WidgetDocument> - 파일 기반 저장소
 * 2. WidgetDataStore - DataStore wrapper
 * 3. WidgetDocumentLocalDataSource - 데이터 소스 추상화 (현재: DataStore 구현)
 * 4. WidgetRepository - Repository 인터페이스 (구현체: WidgetRepositoryImpl)
 * 
 * 아키텍처:
 * ```
 * WidgetRepository (interface)
 *    ↓ (bind)
 * WidgetRepositoryImpl
 *    ↓ (inject)
 * WidgetDocumentLocalDataSource (interface)
 *    ↓ (bind)
 * WidgetDataStoreSource
 *    ↓ (inject)
 * WidgetDataStore
 *    ↓ (inject)
 * DataStore<WidgetDocument>
 * ```
 * 
 * 향후 Room 확장 시:
 * - WidgetRoomSource 구현 추가
 * - bindLocalDataSource를 WidgetRoomSource로 변경 (또는 조건부 제공)
 * - 다른 코드 변경 없음
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    
    /**
     * WidgetRepository 인터페이스를 WidgetRepositoryImpl로 바인딩
     * 
     * @Binds는 인터페이스를 구현체에 연결할 때 사용합니다.
     * Singleton 스코프로 앱 전체에서 단일 인스턴스를 공유합니다.
     */
    @Binds
    @Singleton
    abstract fun bindWidgetRepository(
        impl: WidgetRepositoryImpl
    ): WidgetRepository
    
    /**
     * WidgetDocumentLocalDataSource 인터페이스를 WidgetDataStoreSource로 바인딩
     * 
     * MVP에서는 DataStore 구현체를 사용합니다.
     * 향후 Room으로 전환 시 이 메서드만 수정하면 됩니다:
     * 
     * ```kotlin
     * @Binds
     * @Singleton
     * abstract fun bindLocalDataSource(
     *     impl: WidgetRoomSource  // ← 이 부분만 변경
     * ): WidgetDocumentLocalDataSource
     * ```
     */
    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        impl: WidgetDataStoreSource
    ): WidgetDocumentLocalDataSource
    
    companion object {
        
        /**
         * Context extension property로 생성된 DataStore를 제공
         * 
         * 이 패턴은 Android DataStore 공식 권장 방식입니다.
         * Singleton으로 앱 전체에서 하나의 인스턴스만 사용합니다.
         * 
         * 파일 위치: {app_internal_storage}/datastore/widget_document.pb
         */
        private val Context.widgetDocumentDataStore: DataStore<WidgetDocument> by dataStore(
            fileName = "widget_document.pb",
            serializer = WidgetDocumentSerializer
        )
        
        /**
         * DataStore<WidgetDocument> 제공
         * 
         * ApplicationContext에서 DataStore 인스턴스를 가져와 제공합니다.
         * WidgetDataStore에서 이 DataStore를 주입받아 사용합니다.
         */
        @Provides
        @Singleton
        fun provideWidgetDocumentDataStore(
            @ApplicationContext context: Context
        ): DataStore<WidgetDocument> {
            return context.widgetDocumentDataStore
        }
    }
}


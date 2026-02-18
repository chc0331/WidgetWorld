package com.android.widgetworld.data.datasource

import android.util.Log
import com.android.widgetworld.data.datastore.WidgetDataStore
import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DataStore 기반 WidgetDocument 로컬 데이터 소스 구현체
 * 
 * Proto DataStore를 사용하여 단일 위젯 문서를 관리합니다.
 * MVP에서는 이 구현체만 사용하며, 향후 Room 추가 시에도
 * 이 클래스는 그대로 유지되고 새로운 RoomSource가 추가됩니다.
 * 
 * 저장 위치: `{app_internal_storage}/datastore/widget_document.pb`
 * 
 * 특징:
 * - 타입 안전성: Proto 사용
 * - 원자성: DataStore의 트랜잭션 보장
 * - 반응형: Flow로 실시간 업데이트
 * 
 * @param dataStore Proto DataStore wrapper
 */
@Singleton
class WidgetDataStoreSource @Inject constructor(
    private val dataStore: WidgetDataStore
) : WidgetDocumentLocalDataSource {
    
    companion object {
        private const val TAG = "WidgetDataStoreSource"
    }
    
    /**
     * DataStore에 저장된 문서를 반응형으로 관찰합니다.
     * 
     * DataStore는 단일 문서만 관리하므로,
     * 저장된 문서가 곧 "현재 문서"입니다.
     */
    override fun observeCurrentDocument(): Flow<WidgetDocument> {
        Log.d(TAG, "observeCurrentDocument: DataStore Flow 구독")
        return dataStore.data
    }
    
    /**
     * DataStore의 문서를 원자적으로 업데이트합니다.
     * 
     * DataStore의 updateData는 트랜잭션 방식으로 동작하여
     * Race condition을 자동으로 방지합니다.
     */
    override suspend fun updateCurrentDocument(
        transform: (WidgetDocument) -> WidgetDocument
    ): WidgetDocument {
        Log.d(TAG, "updateCurrentDocument: DataStore 업데이트 시작")
        
        val updatedDocument = dataStore.updateData(transform)
        
        Log.d(TAG, "updateCurrentDocument: 업데이트 완료 (layoutType=${updatedDocument.layoutType}, components=${updatedDocument.uiListCount})")
        return updatedDocument
    }
    
    /**
     * DataStore의 현재 문서를 한 번만 가져옵니다.
     * 
     * Flow의 first()를 사용하여 즉시 값을 반환합니다.
     */
    override suspend fun getCurrentDocument(): WidgetDocument {
        Log.d(TAG, "getCurrentDocument: DataStore에서 현재 문서 조회")
        
        val document = dataStore.data.first()
        
        Log.d(TAG, "getCurrentDocument: 조회 완료 (layoutType=${document.layoutType}, components=${document.uiListCount})")
        return document
    }
}


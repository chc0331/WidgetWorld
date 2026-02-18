package com.android.widgetworld.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.android.widgetworld.proto.WidgetDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WidgetDocument를 위한 Proto DataStore Wrapper
 * 
 * Hilt가 제공하는 DataStore<WidgetDocument> 인스턴스를 감싸서 사용하기 편한 API를 제공합니다.
 * 
 * 저장 위치: `{app_internal_storage}/datastore/widget_document.pb`
 * 
 * DataStore 특징:
 * - 타입 안전성: Proto 타입을 직접 사용
 * - 원자성: updateData는 트랜잭션 방식으로 동작
 * - 반응형: Flow로 변경사항을 실시간 관찰
 * - 코루틴 기반: suspend 함수로 비동기 처리
 * 
 * 사용 예시:
 * ```
 * // 데이터 관찰
 * widgetDataStore.data.collect { document ->
 *     println("Current layout: ${document.layoutType}")
 * }
 * 
 * // 데이터 업데이트
 * widgetDataStore.updateData { currentDocument ->
 *     currentDocument.withLayoutType(LayoutType.LARGE)
 * }
 * ```
 */
@Singleton
class WidgetDataStore @Inject constructor(
    private val dataStore: DataStore<WidgetDocument>
) {
    /**
     * WidgetDocument의 Flow
     * 
     * 데이터가 변경될 때마다 새로운 값이 emit됩니다.
     * Repository에서 이 Flow를 그대로 노출하여 ViewModel이 관찰할 수 있습니다.
     */
    val data: Flow<WidgetDocument> = dataStore.data
    
    /**
     * WidgetDocument를 원자적으로 업데이트합니다.
     * 
     * DataStore의 updateData는 트랜잭션 방식으로 동작하여:
     * - 읽기-수정-쓰기를 하나의 원자적 작업으로 처리
     * - Race condition 자동 방지
     * - 실패 시 자동 재시도
     * 
     * @param transform 현재 문서를 받아 변경된 문서를 반환하는 함수
     * @return 업데이트된 WidgetDocument
     * @throws IOException 파일 I/O 에러 발생 시
     * @throws Exception 기타 예외 발생 시
     * 
     * 사용 예시:
     * ```
     * widgetDataStore.updateData { currentDoc ->
     *     currentDoc.addComponent(newComponent)
     * }
     * ```
     */
    suspend fun updateData(
        transform: suspend (WidgetDocument) -> WidgetDocument
    ): WidgetDocument {
        return dataStore.updateData(transform)
    }
}


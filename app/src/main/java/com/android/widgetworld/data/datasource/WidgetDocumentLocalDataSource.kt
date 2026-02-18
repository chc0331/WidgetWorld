package com.android.widgetworld.data.datasource

import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow

/**
 * WidgetDocument의 로컬 데이터 소스 추상화 인터페이스
 * 
 * 데이터 저장 방식(DataStore, Room 등)을 추상화하여
 * Repository가 구체적인 저장 메커니즘을 알 필요 없게 합니다.
 * 
 * 구현체:
 * - [현재] WidgetDataStoreSource: DataStore 기반 (단일 문서)
 * - [향후] WidgetRoomSource: Room 기반 (다중 문서 + 쿼리)
 * 
 * 확장 시나리오:
 * 1. MVP (현재): DataStoreSource만 사용
 * 2. 다중 위젯 지원: DataStoreSource(현재 문서 ID) + RoomSource(전체 문서)
 * 3. 복합 전략: RoomSource를 primary로, DataStoreSource를 cache로 사용
 * 
 * 이 추상화를 통해 Repository는 변경 없이 구현체만 교체하여
 * 다양한 저장 전략을 적용할 수 있습니다.
 */
interface WidgetDocumentLocalDataSource {
    
    /**
     * 현재 활성화된 WidgetDocument를 반응형으로 관찰합니다.
     * 
     * 구현 방식:
     * - DataStore: 저장된 단일 문서 반환
     * - Room: 현재 선택된 문서 ID를 기준으로 특정 문서 반환
     * 
     * @return WidgetDocument의 Flow
     */
    fun observeCurrentDocument(): Flow<WidgetDocument>
    
    /**
     * 현재 활성화된 WidgetDocument를 원자적으로 업데이트합니다.
     * 
     * 구현 방식:
     * - DataStore: updateData 사용
     * - Room: transaction으로 읽기-수정-쓰기
     * 
     * @param transform 변환 함수
     * @throws Exception 저장 실패 시
     */
    suspend fun updateCurrentDocument(
        transform: (WidgetDocument) -> WidgetDocument
    ): WidgetDocument
    
    /**
     * 현재 활성화된 WidgetDocument를 한 번만 가져옵니다.
     * 
     * @return 현재 WidgetDocument
     * @throws Exception 조회 실패 시
     */
    suspend fun getCurrentDocument(): WidgetDocument
}


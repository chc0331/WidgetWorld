package com.android.widgetworld.domain.repository

import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow

/**
 * WidgetDocument 저장/조회를 위한 Repository 인터페이스
 * 
 * Domain 레이어의 인터페이스로, 실제 구현은 data 레이어에서 제공됩니다.
 * Clean Architecture 원칙에 따라 Domain은 Data의 구현 세부사항을 알지 못합니다.
 * 
 * 구현체: data.repository.WidgetRepositoryImpl (섹션 3에서 구현 예정)
 */
interface WidgetRepository {
    
    /**
     * WidgetDocument를 반응형으로 관찰합니다.
     * 
     * 문서가 변경될 때마다 새로운 값이 emit되며,
     * ViewModel에서 StateFlow로 변환하여 UI에 실시간 반영할 수 있습니다.
     * 
     * @return WidgetDocument의 Flow
     * 
     * 사용 예시:
     * ```
     * val document: StateFlow<WidgetDocument> = 
     *     repository.observeWidgetDocument()
     *         .stateIn(viewModelScope, SharingStarted.Eagerly, emptyWidgetDocument())
     * ```
     */
    fun observeWidgetDocument(): Flow<WidgetDocument>
    
    /**
     * WidgetDocument를 원자적으로 업데이트합니다.
     * 
     * DataStore의 updateData 패턴을 따라 race condition을 방지하고,
     * 읽기-수정-쓰기를 하나의 트랜잭션으로 처리합니다.
     * 
     * @param transform 현재 문서를 받아 변경된 문서를 반환하는 함수
     * @return 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception)
     * 
     * 사용 예시:
     * ```
     * repository.updateWidgetDocument { currentDoc ->
     *     currentDoc.addComponent(newComponent)
     * }
     * ```
     */
    suspend fun updateWidgetDocument(
        transform: (WidgetDocument) -> WidgetDocument
    ): Result<Unit>
    
    /**
     * 현재 WidgetDocument를 한 번만 가져옵니다.
     * 
     * Flow 구독 없이 현재 상태만 필요할 때 사용합니다.
     * (예: 초기 로딩, 일회성 조회)
     * 
     * @return 성공 시 Result.success(WidgetDocument), 실패 시 Result.failure(Exception)
     */
    suspend fun getWidgetDocument(): Result<WidgetDocument>
}


package com.android.widgetworld.domain.repository

import com.android.widgetworld.domain.model.WidgetDocumentHistoryItem
import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * WidgetDocument 저장/조회를 위한 Repository 인터페이스
 * 
 * Domain 레이어의 인터페이스로, 실제 구현은 data 레이어에서 제공됩니다.
 * Clean Architecture 원칙에 따라 Domain은 Data의 구현 세부사항을 알지 못합니다.
 * 
 * 구현체: data.repository.WidgetRepositoryImpl (섹션 3에서 구현 완료)
 * 
 * 향후 확장:
 * - 히스토리 관리 (Undo/Redo, 버전 목록)
 * - 하이브리드 저장소 (DataStore + Room)
 * - 썸네일 생성 및 관리
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
    
    // ============================================================================
    // 향후 구현: 히스토리 관리 (하이브리드 저장소)
    // ============================================================================
    
    /**
     * Undo - 이전 상태로 되돌리기
     * 
     * 향후 구현 예정 (하이브리드 저장소 섹션):
     * - 메모리 Undo 스택에서 이전 상태 로드
     * - DataStore에 즉시 업데이트
     * - UI 즉시 반영
     * 
     * 현재 상태: 미구현 (NotImplementedError 반환)
     * 
     * @return 성공 시 Result.success(이전 문서), 되돌릴 상태가 없으면 Result.success(null)
     */
    suspend fun undo(): Result<WidgetDocument?> {
        return Result.failure(
            NotImplementedError("Undo 기능은 하이브리드 저장소 섹션에서 구현 예정입니다.")
        )
    }
    
    /**
     * Redo - 되돌린 상태를 다시 복원
     * 
     * 향후 구현 예정 (하이브리드 저장소 섹션):
     * - 메모리 Redo 스택에서 복원 상태 로드
     * - DataStore에 즉시 업데이트
     * - UI 즉시 반영
     * 
     * 현재 상태: 미구현 (NotImplementedError 반환)
     * 
     * @return 성공 시 Result.success(복원 문서), 복원할 상태가 없으면 Result.success(null)
     */
    suspend fun redo(): Result<WidgetDocument?> {
        return Result.failure(
            NotImplementedError("Redo 기능은 하이브리드 저장소 섹션에서 구현 예정입니다.")
        )
    }
    
    /**
     * 히스토리 목록 관찰
     * 
     * 향후 구현 예정 (하이브리드 저장소 섹션):
     * - Room에서 저장된 히스토리 로드
     * - 썸네일과 함께 표시
     * - 수정 시간 역순으로 정렬
     * 
     * 현재 상태: 기본 구현 (빈 리스트 반환)
     * 
     * @return 히스토리 아이템 리스트의 Flow
     * 
     * 사용 예시 (향후):
     * ```
     * repository.observeDocumentHistory().collect { historyList ->
     *     // 히스토리 목록 UI 업데이트
     * }
     * ```
     */
    fun observeDocumentHistory(): Flow<List<WidgetDocumentHistoryItem>> {
        return flowOf(emptyList())  // 기본 구현: 빈 리스트
    }
    
    /**
     * 특정 히스토리 버전으로 복원
     * 
     * 향후 구현 예정 (하이브리드 저장소 섹션):
     * - Room에서 특정 ID의 히스토리 로드
     * - 현재 상태를 Undo 스택에 저장
     * - DataStore 업데이트
     * - Room에 현재 문서로 표시
     * 
     * 현재 상태: 미구현 (NotImplementedError 반환)
     * 
     * @param historyId 복원할 히스토리 아이템의 ID
     * @return 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception)
     * 
     * 사용 예시 (향후):
     * ```
     * repository.restoreFromHistory(selectedHistoryId)
     *     .onSuccess {
     *         // 복원 성공
     *     }
     *     .onFailure { exception ->
     *         // 에러 처리
     *     }
     * ```
     */
    suspend fun restoreFromHistory(historyId: String): Result<Unit> {
        return Result.failure(
            NotImplementedError("히스토리 복원 기능은 하이브리드 저장소 섹션에서 구현 예정입니다.")
        )
    }
}


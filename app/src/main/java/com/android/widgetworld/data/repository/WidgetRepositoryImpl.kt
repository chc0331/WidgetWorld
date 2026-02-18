package com.android.widgetworld.data.repository

import android.util.Log
import com.android.widgetworld.data.datasource.WidgetDocumentLocalDataSource
import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WidgetRepository의 구현체
 * 
 * LocalDataSource 추상화를 통해 저장 메커니즘과 분리된 Repository입니다.
 * 
 * 아키텍처 레이어:
 * ```
 * Domain (WidgetRepository interface)
 *    ↓
 * Data (WidgetRepositoryImpl) ← 이 클래스
 *    ↓
 * DataSource (WidgetDocumentLocalDataSource interface)
 *    ↓
 * [현재] WidgetDataStoreSource (DataStore 구현)
 * [향후] WidgetRoomSource (Room 구현)
 * ```
 * 
 * MVP 구현:
 * - LocalDataSource = WidgetDataStoreSource (단일 문서)
 * - 단순하고 빠른 저장/조회
 * 
 * 향후 확장 시나리오:
 * 
 * 1. **다중 위젯 관리**:
 *    - LocalDataSource = WidgetRoomSource
 *    - 추가 Repository: WidgetListRepository (목록/검색)
 *    - DataStore는 "현재 선택된 문서 ID" 저장용으로 활용
 * 
 * 2. **하이브리드 전략**:
 *    - Room: Primary storage (영속성, 복잡한 쿼리)
 *    - DataStore: Cache/Settings (빠른 접근, 단순 데이터)
 *    - Repository에서 두 소스를 조합
 * 
 * 3. **오프라인 동기화**:
 *    - Room: 로컬 DB
 *    - RemoteDataSource: 서버 API
 *    - Repository에서 동기화 로직 처리
 * 
 * 이 구조의 장점:
 * - Repository 코드 변경 최소화 (DI만 바꾸면 됨)
 * - 테스트 용이 (DataSource를 Mock 가능)
 * - 저장 전략 유연하게 교체 가능
 * 
 * @param localDataSource 로컬 데이터 소스 (DataStore 또는 Room)
 */
@Singleton
class WidgetRepositoryImpl @Inject constructor(
    private val localDataSource: WidgetDocumentLocalDataSource
) : WidgetRepository {
    
    companion object {
        private const val TAG = "WidgetRepositoryImpl"
    }
    
    /**
     * WidgetDocument를 반응형으로 관찰합니다.
     * 
     * LocalDataSource의 Flow를 그대로 반환하여 ViewModel에서 StateFlow로 변환할 수 있습니다.
     * 문서가 업데이트될 때마다 자동으로 새로운 값이 emit됩니다.
     * 
     * @return WidgetDocument의 Flow (Hot Stream)
     * 
     * 사용 예시:
     * ```
     * // ViewModel에서
     * val widgetDocument: StateFlow<WidgetDocument> = 
     *     repository.observeWidgetDocument()
     *         .stateIn(viewModelScope, SharingStarted.Eagerly, emptyWidgetDocument())
     * ```
     */
    override fun observeWidgetDocument(): Flow<WidgetDocument> {
        Log.d(TAG, "observeWidgetDocument: Flow 구독 시작")
        return localDataSource.observeCurrentDocument()
    }
    
    /**
     * WidgetDocument를 원자적으로 업데이트합니다.
     * 
     * LocalDataSource를 통해 트랜잭션 방식으로 업데이트합니다:
     * - 읽기-수정-쓰기가 원자적으로 처리됨
     * - Race condition 자동 방지
     * - 실패 시 자동 재시도 (DataSource 구현에 따라)
     * 
     * 모든 예외를 Result로 래핑하여 Domain 레이어가 안전하게 처리할 수 있도록 합니다.
     * 
     * @param transform 현재 문서를 받아 변경된 문서를 반환하는 함수
     * @return 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception)
     * 
     * 사용 예시:
     * ```
     * // UseCase에서
     * repository.updateWidgetDocument { currentDoc ->
     *     currentDoc.addComponent(newComponent)
     * }.onSuccess {
     *     // 저장 성공
     * }.onFailure { exception ->
     *     // 에러 처리
     * }
     * ```
     */
    override suspend fun updateWidgetDocument(
        transform: (WidgetDocument) -> WidgetDocument
    ): Result<Unit> {
        return try {
            Log.d(TAG, "updateWidgetDocument: 업데이트 시작")
            
            // LocalDataSource의 updateCurrentDocument 호출
            val updatedDocument = localDataSource.updateCurrentDocument(transform)
            
            Log.d(TAG, "updateWidgetDocument: 업데이트 성공 (layoutType=${updatedDocument.layoutType}, components=${updatedDocument.uiListCount})")
            Result.success(Unit)
            
        } catch (e: Exception) {
            // 모든 예외를 Result.failure로 변환
            // IOException, CorruptionException, 기타 예외 모두 포함
            Log.e(TAG, "updateWidgetDocument: 업데이트 실패", e)
            Result.failure(e)
        }
    }
    
    /**
     * 현재 WidgetDocument를 한 번만 가져옵니다.
     * 
     * Flow 구독 없이 현재 상태만 필요할 때 사용합니다.
     * LocalDataSource를 통해 현재 문서를 가져옵니다.
     * 
     * 사용 시나리오:
     * - 초기 로딩 시 현재 문서 확인
     * - 일회성 조회가 필요한 경우
     * - 검증 로직에서 현재 상태 확인
     * 
     * @return 성공 시 Result.success(WidgetDocument), 실패 시 Result.failure(Exception)
     * 
     * 사용 예시:
     * ```
     * // UseCase에서
     * repository.getWidgetDocument()
     *     .onSuccess { document ->
     *         // 현재 문서 사용
     *     }
     *     .onFailure { exception ->
     *         // 에러 처리
     *     }
     * ```
     */
    override suspend fun getWidgetDocument(): Result<WidgetDocument> {
        return try {
            Log.d(TAG, "getWidgetDocument: 현재 문서 조회 시작")
            
            // LocalDataSource에서 현재 문서 가져오기
            val document = localDataSource.getCurrentDocument()
            
            Log.d(TAG, "getWidgetDocument: 조회 성공 (layoutType=${document.layoutType}, components=${document.uiListCount})")
            Result.success(document)
            
        } catch (e: Exception) {
            // 모든 예외를 Result.failure로 변환
            Log.e(TAG, "getWidgetDocument: 조회 실패", e)
            Result.failure(e)
        }
    }
}


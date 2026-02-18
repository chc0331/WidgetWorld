package com.android.widgetworld.domain.usecase

import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.WidgetDocument
import javax.inject.Inject

/**
 * WidgetDocument를 저장하는 UseCase (전체 덮어쓰기)
 * 
 * 새로운 WidgetDocument로 전체를 교체합니다.
 * 부분 업데이트가 아닌 전체 문서 교체가 필요할 때 사용합니다.
 * 
 * 참고:
 * - 부분 업데이트(컴포넌트 추가, 레이아웃 변경 등)는 각각의 전용 UseCase를 사용
 * - 이 UseCase는 문서 전체 초기화나 외부 문서 로드 시 사용
 * 
 * 사용 예시:
 * ```
 * viewModelScope.launch {
 *     val result = saveWidgetDocument(newDocument)
 *     result.onSuccess {
 *         // 저장 성공
 *     }.onFailure { exception ->
 *         // 에러 처리
 *     }
 * }
 * ```
 */
class SaveWidgetDocumentUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    /**
     * WidgetDocument를 저장합니다.
     * 
     * @param document 저장할 WidgetDocument (기존 문서를 완전히 교체)
     * @return 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception)
     */
    suspend operator fun invoke(document: WidgetDocument): Result<Unit> {
        return repository.updateWidgetDocument { document }
    }
}

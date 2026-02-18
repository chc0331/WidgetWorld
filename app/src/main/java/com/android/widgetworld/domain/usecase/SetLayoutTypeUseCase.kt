package com.android.widgetworld.domain.usecase

import com.android.widgetworld.domain.model.withLayoutType
import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.LayoutType
import javax.inject.Inject

/**
 * WidgetDocument의 레이아웃 타입을 변경하는 UseCase
 * 
 * 사용자가 레이아웃 타입(MEDIUM/LARGE/FULL)을 선택하면
 * 문서의 layout_type 필드를 업데이트합니다.
 * 
 * 기존 컴포넌트들은 그대로 유지되며, 레이아웃 타입만 변경됩니다.
 * (향후 레이아웃 변경 시 컴포넌트 위치 검증 로직 추가 가능)
 * 
 * PRD 참조: 섹션 5-2 "Layout 컴포넌트 선택 → WidgetCanvas 배치"
 * 
 * 사용 예시:
 * ```
 * viewModelScope.launch {
 *     val result = setLayoutType(LayoutType.MEDIUM)
 *     result.onSuccess {
 *         // Canvas에 레이아웃 컨테이너 표시
 *     }.onFailure { exception ->
 *         // 에러 처리
 *     }
 * }
 * ```
 */
class SetLayoutTypeUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    /**
     * 레이아웃 타입을 변경합니다.
     * 
     * @param layoutType 변경할 레이아웃 타입 (MEDIUM, LARGE, FULL)
     * @return 성공 시 Result.success(Unit), 실패 시 Result.failure(Exception)
     */
    suspend operator fun invoke(layoutType: LayoutType): Result<Unit> {
        return repository.updateWidgetDocument { currentDoc ->
            currentDoc.withLayoutType(layoutType)
        }
    }
}

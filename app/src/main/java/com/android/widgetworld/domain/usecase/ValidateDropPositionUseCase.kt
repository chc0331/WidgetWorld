package com.android.widgetworld.domain.usecase

import com.android.widgetworld.proto.Position
import javax.inject.Inject

/**
 * Drop 위치가 레이아웃 영역 내부인지 검증하는 UseCase
 * 
 * Drag&Drop 중 컴포넌트가 레이아웃 영역 내부에 있는지 체크합니다.
 * Dragging 중에는 프리뷰/하이라이트를 표시하고,
 * Drop 시에는 최종 검증을 수행합니다.
 * 
 * PRD 참조:
 * - 섹션 5-3-3-1: "Layout 영역 Drop 가능 여부 최종 체크"
 * - 섹션 5-4: "Layout 밖 Drop 불가 처리"
 * 
 * 사용 예시:
 * ```
 * // Dragging 중
 * val isValid = validateDropPosition(dragPosition, layoutBounds)
 * if (isValid) {
 *     // 초록색 하이라이트 표시
 * } else {
 *     // 빨간색 경고 표시
 * }
 * 
 * // Drop 시
 * if (!validateDropPosition(dropPosition, layoutBounds)) {
 *     // Drop 취소, 에러 메시지 표시
 *     return
 * }
 * ```
 */
class ValidateDropPositionUseCase @Inject constructor() {
    /**
     * 컴포넌트 Position이 레이아웃 영역 내부에 있는지 검증합니다.
     * 
     * @param componentPosition 컴포넌트의 위치/크기 (Layout 기준 좌표)
     * @param layoutBounds 레이아웃 영역의 경계
     * @return true: 레이아웃 내부, false: 레이아웃 밖
     */
    operator fun invoke(
        componentPosition: Position,
        layoutBounds: Position
    ): Boolean {
        // 컴포넌트의 모든 모서리가 레이아웃 영역 내부에 있어야 함
        val componentRight = componentPosition.x + componentPosition.width
        val componentBottom = componentPosition.y + componentPosition.height
        val layoutRight = layoutBounds.x + layoutBounds.width
        val layoutBottom = layoutBounds.y + layoutBounds.height
        
        return componentPosition.x >= layoutBounds.x &&
                componentPosition.y >= layoutBounds.y &&
                componentRight <= layoutRight &&
                componentBottom <= layoutBottom
    }
}

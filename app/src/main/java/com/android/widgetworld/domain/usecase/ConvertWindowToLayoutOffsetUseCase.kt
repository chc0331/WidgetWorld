package com.android.widgetworld.domain.usecase

import com.android.widgetworld.proto.Position
import javax.inject.Inject

/**
 * Window 좌표를 Layout 좌표로 변환하는 UseCase
 * 
 * Drag 이벤트는 Window(화면) 기준 좌표로 들어오지만,
 * WidgetDocument에 저장되는 Position은 Layout(컨테이너) 기준 좌표입니다.
 * 이 UseCase는 좌표계를 변환합니다.
 * 
 * PRD 참조:
 * - 섹션 5-3-2-2: "Layout 기준 x,y 좌표 업데이트"
 * - 섹션 5-3-3-2: "Window 기준 x,y 좌표 기준으로 Drop 처리"
 * 
 * 좌표 변환 공식:
 * - layoutX = windowX - layoutBounds.x
 * - layoutY = windowY - layoutBounds.y
 * 
 * 사용 예시:
 * ```
 * // Dragging 이벤트
 * val windowOffset = dragEvent.position
 * val (layoutX, layoutY) = convertWindowToLayoutOffset(
 *     windowX = windowOffset.x,
 *     windowY = windowOffset.y,
 *     layoutBounds = canvasLayoutBounds
 * )
 * 
 * // layoutX, layoutY를 사용하여 프리뷰 표시
 * ```
 */
class ConvertWindowToLayoutOffsetUseCase @Inject constructor() {
    /**
     * Window 좌표를 Layout 좌표로 변환합니다.
     * 
     * @param windowX Window 기준 x 좌표
     * @param windowY Window 기준 y 좌표
     * @param layoutBounds Layout 영역의 경계 (x, y는 Window 기준)
     * @return Pair(layoutX, layoutY) - Layout 기준 좌표
     */
    operator fun invoke(
        windowX: Float,
        windowY: Float,
        layoutBounds: Position
    ): Pair<Float, Float> {
        val layoutX = windowX - layoutBounds.x
        val layoutY = windowY - layoutBounds.y
        
        return Pair(layoutX, layoutY)
    }
    
    /**
     * 오버로드: Compose Offset을 직접 받는 버전
     * (향후 Compose UI 레이어에서 사용 편의성)
     */
    operator fun invoke(
        windowOffset: androidx.compose.ui.geometry.Offset,
        layoutBounds: Position
    ): Pair<Float, Float> {
        return invoke(windowOffset.x, windowOffset.y, layoutBounds)
    }
}

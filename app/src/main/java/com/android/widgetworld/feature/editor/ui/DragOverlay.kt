package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import com.android.widgetworld.feature.editor.model.DragState
import kotlin.math.roundToInt

/**
 * Drag Overlay
 * 
 * PRD 참조: 섹션 4-3-2 "Dragging Event → Drag 중 시각 효과"
 * 
 * Drag 중인 컴포넌트를 손가락 위치에 따라 표시합니다.
 * DragState의 dragContent만 렌더링하며, 손가락이 컨텐츠 중앙에 위치합니다.
 * 
 * 구현 방식: onSizeChanged로 실제 측정된 크기를 사용하여 중앙 정렬
 * 측정 전까지는 투명하게 표시하여 깜빡임 방지
 * 
 * @param dragState Drag 상태
 * @param isValidDropPosition Drop 가능 여부 (현재 미사용, LayoutArea에서 처리)
 * @param modifier Modifier
 */
@Composable
fun DragOverlay(
    dragState: DragState?,
    isValidDropPosition: Boolean,
    modifier: Modifier = Modifier
) {
    if (dragState?.isDragging == true) {
        // 컨텐츠의 실제 측정된 크기를 저장
        var contentSize by remember { mutableStateOf(IntSize.Zero) }
        
        // 크기가 측정되었는지 확인
        val isMeasured = contentSize.width > 0 && contentSize.height > 0
        
        Box(
            modifier = modifier
                .onSizeChanged { size ->
                    // 컨텐츠가 측정되면 크기 저장
                    contentSize = size
                }
                .offset {
                    // 손가락 위치 - (컨텐츠 크기 / 2) = 가운데 배치
                    IntOffset(
                        x = dragState.windowOffset.x.roundToInt() - contentSize.width / 2,
                        y = dragState.windowOffset.y.roundToInt() - contentSize.height / 2
                    )
                }
                .zIndex(1000f) // 최상단에 표시
                .graphicsLayer {
                    // 측정 전까지는 투명하게 (깜빡임 방지)
                    // 측정 후에는 0.5 투명도로 표시
                    alpha = if (isMeasured) 0.5f else 0f
                }
        ) {
            // DragState에서 제공하는 컴포저블만 렌더링
            dragState.dragContent()
        }
    }
}


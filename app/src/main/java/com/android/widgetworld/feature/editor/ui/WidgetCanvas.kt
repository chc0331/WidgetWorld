package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.android.widgetworld.core.model.LayoutDimensions
import com.android.widgetworld.proto.LayoutType

/**
 * Widget Canvas - 컴포넌트를 배치할 캔버스 영역
 * 
 * PRD 참조: 섹션 4-2 "WidgetCanvas(컨테이너) 구현"
 * 
 * Canvas는 전체 영역을 차지하며, 그 안에 Layout 영역이 표시됩니다.
 * - Layout 영역: 컴포넌트를 배치할 수 있는 영역 (흰색 배경)
 * - Layout 밖 영역: 배치 불가능한 영역 (회색 배경)
 * 
 * @param layoutType 현재 선택된 Layout 타입
 * @param isDragging Drag 중인지 여부
 * @param isValidDropPosition Drop 가능 여부
 * @param onCanvasBoundsChanged Canvas 경계가 변경될 때 호출되는 콜백
 * @param onDragPositionChanged Drag 위치가 변경될 때 호출되는 콜백
 * @param modifier Modifier
 */
@Composable
fun WidgetCanvas(
    layoutType: LayoutType,
    isDragging: Boolean = false,
    isValidDropPosition: Boolean = false,
    onCanvasBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onGloballyPositioned { coordinates ->
                // Canvas 경계 계산 (Window 좌표계)
                val position = coordinates.positionInWindow()
                val size = coordinates.size.toSize()
                
                val bounds = Rect(
                    left = position.x,
                    top = position.y,
                    right = position.x + size.width,
                    bottom = position.y + size.height
                )
                
                onCanvasBoundsChanged(bounds)
            },
        contentAlignment = Alignment.Center
    ) {
        if (layoutType == LayoutType.LAYOUT_TYPE_UNSPECIFIED) {
            // Layout이 선택되지 않은 상태
            EmptyCanvasGuide()
        } else {
            // Layout 영역 표시
            LayoutArea(
                layoutType = layoutType,
                isDragging = isDragging,
                isValidDropPosition = isValidDropPosition
            )
        }
    }
}

/**
 * 빈 Canvas 가이드
 * 
 * Layout이 선택되지 않았을 때 표시되는 안내 메시지입니다.
 */
@Composable
private fun EmptyCanvasGuide() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Layout을 선택하세요",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "위에서 Layout 크기를 선택하면\n컴포넌트를 배치할 수 있습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Layout 영역
 * 
 * 선택된 Layout 타입에 따라 크기가 결정됩니다.
 * 컴포넌트를 배치할 수 있는 영역으로 시각적으로 구분됩니다.
 * 
 * LayoutDimensions를 사용하여 크기를 결정합니다 (Single Source of Truth).
 * 
 * @param layoutType 현재 선택된 Layout 타입
 * @param isDragging Drag 중인지 여부
 * @param isValidDropPosition Drop 가능 여부 (Layout 영역 내부인지)
 */
@Composable
private fun LayoutArea(
    layoutType: LayoutType,
    isDragging: Boolean = false,
    isValidDropPosition: Boolean = false
) {
    // LayoutDimensions에서 크기 가져오기 (Single Source of Truth)
    val (width, height) = remember(layoutType) {
        LayoutDimensions.getSize(layoutType)
    }
    
    // Drag 중일 때 배경색 변경
    val backgroundColor = when {
        !isDragging -> MaterialTheme.colorScheme.surface
        isValidDropPosition -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    }
    
    val borderColor = when {
        !isDragging -> MaterialTheme.colorScheme.primary
        isValidDropPosition -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }
    
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isDragging) 3.dp else 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = layoutType.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (isDragging && !isValidDropPosition) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            Text(
                text = "${width.value.toInt()} × ${height.value.toInt()} dp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (isDragging) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isValidDropPosition) {
                        "✓ 배치 가능"
                    } else {
                        "✗ 배치 불가"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isValidDropPosition) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "컴포넌트 배치 영역",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


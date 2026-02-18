package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import com.android.widgetworld.core.model.SampleComponents

/**
 * 컴포넌트 팔레트
 *
 * PRD 참조: 섹션 4-3-0 "컴포넌트 팔레트/리스트"
 *
 * 드래그 가능한 컴포넌트 목록을 표시합니다.
 * Long Press로 Drag를 시작할 수 있습니다.
 *
 * @param onComponentLongPress 컴포넌트 Long Press 콜백
 * @param onDragStart Drag 시작 콜백
 * @param onDrag Drag 중 콜백
 * @param onDragEnd Drag 종료 콜백
 * @param modifier Modifier
 */
@Composable
fun ComponentPalette(
    onComponentLongPress: (SampleComponents.ComponentItem) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "컴포넌트",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "컴포넌트를 길게 눌러 드래그하세요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SampleComponents.availableComponents) { component ->
                    ComponentCard(
                        component = component,
                        onLongPress = {
                            onComponentLongPress(component)
                        },
                        onDragStart = onDragStart,
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    )
                }
            }
        }
    }
}

/**
 * 개별 컴포넌트 카드
 *
 * Long Press 제스처를 감지하고 Drag를 처리합니다.
 *
 * @param component 컴포넌트 아이템
 * @param onLongPress Long Press 콜백
 * @param onDragStart Drag 시작 콜백
 * @param onDrag Drag 중 콜백
 * @param onDragEnd Drag 종료 콜백
 */
@Composable
private fun ComponentCard(
    component: SampleComponents.ComponentItem,
    onLongPress: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 카드의 Window 좌표를 저장
    var cardPosition by remember { mutableStateOf(Offset.Zero) }

    ElevatedCard(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                // 카드의 Window 좌표 저장
                cardPosition = coordinates.positionInWindow()
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onLongPress()
                        // offset은 카드 내부 로컬 좌표 → Window 좌표로 변환
                        val windowOffset = Offset(
                            x = cardPosition.x + offset.x,
                            y = cardPosition.y + offset.y
                        )
                        onDragStart(windowOffset)
                    },
                    onDrag = { change, _ ->
                        // change.position은 카드 내부 로컬 좌표 → Window 좌표로 변환
                        val windowOffset = Offset(
                            x = cardPosition.x + change.position.x,
                            y = cardPosition.y + change.position.y
                        )
                        onDrag(windowOffset)
                        change.consume()
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = component.emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = component.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = component.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


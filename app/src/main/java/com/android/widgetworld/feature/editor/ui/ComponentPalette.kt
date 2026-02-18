package com.android.widgetworld.feature.editor.ui

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
 * @param modifier Modifier
 */
@Composable
fun ComponentPalette(
    onComponentLongPress: (SampleComponents.ComponentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                        }
                    )
                }
            }
        }
    }
}

/**
 * 개별 컴포넌트 카드
 *
 * Long Press 제스처를 감지합니다.
 *
 * @param component 컴포넌트 아이템
 * @param onLongPress Long Press 콜백
 */
@Composable
private fun ComponentCard(
    component: SampleComponents.ComponentItem,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .width(100.dp)
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() }
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


package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.widgetworld.core.model.LayoutDimensions
import com.android.widgetworld.proto.LayoutType

/**
 * Layout 타입 선택 탭
 * 
 * PRD 참조: 섹션 4-2 "Layout 컴포넌트 선택 → WidgetCanvas 배치"
 * 
 * 사용자가 MEDIUM/LARGE/FULL 중 하나를 선택할 수 있습니다.
 * 
 * @param selectedLayoutType 현재 선택된 Layout 타입
 * @param onLayoutTypeSelected Layout 타입 선택 콜백
 * @param modifier Modifier
 */
@Composable
fun LayoutTab(
    selectedLayoutType: LayoutType,
    onLayoutTypeSelected: (LayoutType) -> Unit,
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
                text = "Layout 크기 선택",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutTypeButton(
                    layoutType = LayoutType.MEDIUM,
                    label = "Medium",
                    isSelected = selectedLayoutType == LayoutType.MEDIUM,
                    onClick = { onLayoutTypeSelected(LayoutType.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                
                LayoutTypeButton(
                    layoutType = LayoutType.LARGE,
                    label = "Large",
                    isSelected = selectedLayoutType == LayoutType.LARGE,
                    onClick = { onLayoutTypeSelected(LayoutType.LARGE) },
                    modifier = Modifier.weight(1f)
                )
                
                LayoutTypeButton(
                    layoutType = LayoutType.FULL,
                    label = "Full",
                    isSelected = selectedLayoutType == LayoutType.FULL,
                    onClick = { onLayoutTypeSelected(LayoutType.FULL) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Layout 타입 선택 버튼
 * 
 * FilterChip을 사용하여 선택 상태를 표시합니다.
 * LayoutDimensions에서 크기를 가져와 표시합니다 (Single Source of Truth).
 */
@Composable
private fun LayoutTypeButton(
    layoutType: LayoutType,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // LayoutDimensions에서 크기 가져오기 (Single Source of Truth)
    val (width, height) = LayoutDimensions.getSize(layoutType)
    val description = "${width.value.toInt()}×${height.value.toInt()}"
    
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        modifier = modifier
    )
}


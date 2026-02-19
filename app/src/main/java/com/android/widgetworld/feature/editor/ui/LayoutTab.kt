package com.android.widgetworld.feature.editor.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
 * 접기/펼치기 기능을 제공하여 Canvas 영역의 시인성을 높일 수 있습니다.
 *
 * @param selectedLayoutType 현재 선택된 Layout 타입
 * @param onLayoutTypeSelected Layout 타입 선택 콜백
 * @param modifier Modifier
 * @param isExpanded 펼쳐진 상태 여부
 * @param onToggleExpand 접기/펼치기 콜백
 */
@Composable
fun LayoutTab(
    selectedLayoutType: LayoutType,
    onLayoutTypeSelected: (LayoutType) -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onToggleExpand: () -> Unit = {}
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
            // Header with title and expand/collapse button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layout 크기 선택",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = if (isExpanded) "접기" else "펼치기",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Collapsible content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
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


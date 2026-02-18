package com.android.widgetworld.feature.editor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

/**
 * Drag State Handler
 * 
 * Drag offset 관리 로직을 캡슐화한 데이터 클래스입니다.
 * 
 * 재사용 가능한 Compose 패턴으로, 다양한 드래그 시나리오에서 사용할 수 있습니다.
 * - ComponentPalette의 컴포넌트 드래그
 * - WidgetCanvas 내부의 위젯 드래그 (향후)
 * - 기타 드래그 인터랙션
 * 
 * @property getCurrentOffset 현재 드래그 offset을 반환하는 함수
 * @property onDragStart 드래그 시작 시 호출되는 콜백
 * @property onDrag 드래그 중 호출되는 콜백
 * @property onDragEnd 드래그 종료 시 호출되는 콜백
 */
data class DragStateHandler(
    val getCurrentOffset: () -> Offset?,
    val onDragStart: (Offset) -> Unit,
    val onDrag: (Offset) -> Unit,
    val onDragEnd: () -> Unit
)

/**
 * Remember Drag State
 * 
 * 드래그 offset을 관리하는 재사용 가능한 Composable 함수입니다.
 * 
 * Compose의 표준 remember* 패턴을 따르며, 로컬 state를 생성하고
 * DragStateHandler를 통해 캡슐화된 인터페이스를 제공합니다.
 * 
 * **사용 예시:**
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     val dragHandler = rememberDragState()
 *     
 *     DraggableComponent(
 *         onDragStart = dragHandler.onDragStart,
 *         onDrag = dragHandler.onDrag,
 *         onDragEnd = dragHandler.onDragEnd
 *     )
 *     
 *     DragOverlay(offset = dragHandler.getCurrentOffset())
 * }
 * ```
 * 
 * **성능 최적화:**
 * - 로컬 state로 관리되어 ViewModel을 거치지 않음
 * - recomposition 범위가 최소화됨
 * 
 * **테스트 용이성:**
 * - DragStateHandler를 독립적으로 unit test 가능
 * - UI와 분리된 로직 테스트
 * 
 * @return DragStateHandler 드래그 상태를 관리하는 핸들러
 */
@Composable
fun rememberDragState(): DragStateHandler {
    // 현재 드래그 offset을 저장하는 로컬 state
    var currentOffset by remember { mutableStateOf<Offset?>(null) }
    
    // DragStateHandler 생성 (remember로 재생성 방지)
    return remember {
        DragStateHandler(
            getCurrentOffset = { currentOffset },
            onDragStart = { offset -> 
                currentOffset = offset 
            },
            onDrag = { offset -> 
                currentOffset = offset 
            },
            onDragEnd = { 
                currentOffset = null 
            }
        )
    }
}


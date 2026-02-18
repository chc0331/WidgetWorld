package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.widgetworld.feature.editor.viewmodel.EditorUiEvent
import com.android.widgetworld.feature.editor.viewmodel.EditorViewModel

/**
 * Editor 화면
 *
 * PRD 참조: 섹션 4-2 "Layout 컴포넌트 선택 → WidgetCanvas 배치"
 *
 * Layout을 선택하고 컴포넌트를 배치할 수 있는 편집 화면입니다.
 *
 * State Hosting 원칙:
 * - collectAsState()로 State 구독
 * - Event는 ViewModel.handleEvent()로 전달
 * - Drag offset은 rememberDragState()로 로컬 관리 (성능 최적화)
 *
 * @param onNavigateBack 뒤로 가기 콜백
 * @param modifier Modifier
 * @param viewModel EditorViewModel (Hilt 자동 주입)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val density = LocalDensity.current
    
    // Drag offset 관리 (재사용 가능한 remember 패턴)
    val dragHandler = rememberDragState()

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("위젯 에디터") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로 가기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Layout 선택 탭
                LayoutTab(
                    selectedLayoutType = uiState.layoutType,
                    onLayoutTypeSelected = { layoutType ->
                        viewModel.handleEvent(EditorUiEvent.OnLayoutTypeSelected(layoutType))
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )

                // 컴포넌트 팔레트
                ComponentPalette(
                    onComponentLongPress = { component ->
                        viewModel.handleEvent(
                            EditorUiEvent.OnComponentLongPress(
                                component = component,
                                dragContent = {
                                    // Drag 중 표시할 컨텐츠 (간단한 프리뷰)
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .background(androidx.compose.ui.graphics.Color.Black)
                                    )
                                }
                            )
                        )
                    },
                    onDragStart = dragHandler.onDragStart,
                    onDrag = dragHandler.onDrag,
                    onDragEnd = {
                        dragHandler.onDragEnd()
                        viewModel.handleEvent(EditorUiEvent.OnDragEnd)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )

                // Widget Canvas
                WidgetCanvas(
                    layoutType = uiState.layoutType,
                    isDragging = uiState.isDragging,
                    isValidDropPosition = uiState.isValidDropPosition,
                    onCanvasBoundsChanged = { bounds ->
                        viewModel.handleCanvasBoundsChanged(bounds, density)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 에러 메시지 표시 (Snackbar)
            uiState.errorMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(message)
                }
            }
        }

        // Drag Overlay (화면 최상단)
        // dragHandler의 offset과 ViewModel의 dragState 결합
        val displayDragState = uiState.dragState?.let { state ->
            dragHandler.getCurrentOffset()?.let { offset ->
                // Drag 중: 로컬 offset 사용 (실시간 업데이트)
                state.copy(windowOffset = offset)
            } ?: state  // Drag 중이 아니면 ViewModel의 state 그대로
        }
        
        DragOverlay(
            dragState = displayDragState,
            isValidDropPosition = uiState.isValidDropPosition
        )
    }
}


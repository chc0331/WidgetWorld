package com.android.widgetworld.feature.editor.viewmodel

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.widgetworld.core.model.LayoutDimensions
import com.android.widgetworld.domain.usecase.LoadWidgetDocumentUseCase
import com.android.widgetworld.domain.usecase.SetLayoutTypeUseCase
import com.android.widgetworld.proto.LayoutType
import com.android.widgetworld.proto.WidgetDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Editor 화면의 UI 상태
 * 
 * State Hosting 원칙: 불변 data class
 * 
 * @property widgetDocument 현재 편집 중인 위젯 문서
 * @property canvasBounds Canvas 영역의 경계 (Window 좌표계)
 * @property layoutBounds Layout 영역의 경계 (Canvas 내부, Layout 좌표계)
 * @property isLoading 문서 로딩 중 여부
 * @property errorMessage 에러 메시지 (nullable)
 */
data class EditorUiState(
    val widgetDocument: WidgetDocument = WidgetDocument.getDefaultInstance(),
    val canvasBounds: Rect? = null,
    val layoutBounds: Rect? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    /**
     * 현재 선택된 Layout 타입
     */
    val layoutType: LayoutType
        get() = widgetDocument.layoutType
    
    /**
     * Layout이 선택되었는지 여부
     */
    val hasLayout: Boolean
        get() = layoutType != LayoutType.LAYOUT_TYPE_UNSPECIFIED
}

/**
 * Editor 화면의 UI 이벤트
 * 
 * State Hosting 원칙: sealed interface
 * Composable에서 ViewModel로 전달되는 모든 사용자 액션
 */
sealed interface EditorUiEvent {
    /**
     * Layout 타입 선택
     * 
     * @property layoutType 선택한 Layout 타입 (MEDIUM/LARGE/FULL)
     */
    data class OnLayoutTypeSelected(val layoutType: LayoutType) : EditorUiEvent
}

/**
 * Editor 화면 ViewModel
 * 
 * PRD 참조: 섹션 4-2 "Layout 컴포넌트 선택 → WidgetCanvas 배치"
 * 
 * State Hosting 원칙 준수:
 * - UI State는 StateFlow로 노출
 * - UI Event는 handleEvent()로 처리
 * - State는 copy()로만 업데이트
 * - Repository와 연동하여 자동 저장/로드
 */
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val loadWidgetDocument: LoadWidgetDocumentUseCase,
    private val setLayoutType: SetLayoutTypeUseCase
) : ViewModel() {
    
    // Canvas/Layout Bounds는 UI에서 측정되므로 별도 State로 관리
    private val _localState = MutableStateFlow(LocalEditorState())
    
    /**
     * UI 상태
     * 
     * WidgetDocument Flow와 Local State를 결합하여 최종 UI State를 생성합니다.
     */
    val uiState: StateFlow<EditorUiState> = combine(
        loadWidgetDocument(),
        _localState
    ) { document, localState ->
        EditorUiState(
            widgetDocument = document,
            canvasBounds = localState.canvasBounds,
            layoutBounds = localState.layoutBounds,
            isLoading = false,
            errorMessage = localState.errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState(isLoading = true)
    )
    
    /**
     * UI 이벤트 처리
     * 
     * Composable에서 이 메서드를 호출하여 사용자 액션을 전달합니다.
     * 
     * @param event 처리할 UI 이벤트
     */
    fun handleEvent(event: EditorUiEvent) {
        when (event) {
            is EditorUiEvent.OnLayoutTypeSelected -> {
                handleLayoutTypeSelected(event.layoutType)
            }
        }
    }
    
    /**
     * Layout 타입 선택 처리
     * 
     * SetLayoutTypeUseCase를 사용하여 DataStore에 저장합니다.
     * UseCase 내부에서 Repository를 통해 자동 저장되며,
     * Flow가 업데이트되어 UI에 자동 반영됩니다.
     */
    private fun handleLayoutTypeSelected(layoutType: LayoutType) {
        viewModelScope.launch {
            setLayoutType(layoutType)
                .onSuccess {
                    // 저장 성공 - Flow가 자동으로 업데이트됨
                    _localState.update { it.copy(errorMessage = null) }
                }
                .onFailure { exception ->
                    // 에러 처리
                    _localState.update {
                        it.copy(errorMessage = "Layout 저장 실패: ${exception.message}")
                    }
                }
        }
    }
    
    /**
     * Canvas 경계 변경 처리
     * 
     * Canvas 크기가 변경되면 Layout 경계도 재계산합니다.
     * Layout 경계는 Canvas 내부에 고정된 크기로 표시됩니다.
     * 
     * @param canvasBounds Canvas 경계
     * @param density 현재 화면 density (dp → px 변환용)
     */
    fun handleCanvasBoundsChanged(canvasBounds: Rect, density: Density) {
        val layoutBounds = calculateLayoutBounds(
            canvasBounds = canvasBounds,
            layoutType = uiState.value.layoutType,
            density = density
        )
        
        _localState.update {
            it.copy(
                canvasBounds = canvasBounds,
                layoutBounds = layoutBounds
            )
        }
    }
    
    /**
     * Layout 경계 계산
     * 
     * LayoutDimensions에서 정의된 크기를 사용하여 Layout 경계를 계산합니다.
     * Canvas 중앙에 배치됩니다.
     * 
     * @param canvasBounds Canvas 경계
     * @param layoutType Layout 타입
     * @param density 화면 density (dp → px 변환용)
     * @return Layout 경계 (px 단위), 타입이 UNSPECIFIED면 null
     */
    private fun calculateLayoutBounds(
        canvasBounds: Rect,
        layoutType: LayoutType,
        density: Density
    ): Rect? {
        if (layoutType == LayoutType.LAYOUT_TYPE_UNSPECIFIED) {
            return null
        }
        
        // LayoutDimensions에서 크기 가져오기 (Single Source of Truth)
        val (widthDp, heightDp) = LayoutDimensions.getSize(layoutType)
        
        // Dp → Px 변환
        val width = with(density) { widthDp.toPx() }
        val height = with(density) { heightDp.toPx() }
        
        // Canvas 중앙에 배치
        val left = (canvasBounds.width - width) / 2f
        val top = (canvasBounds.height - height) / 2f
        
        return Rect(
            left = left,
            top = top,
            right = left + width,
            bottom = top + height
        )
    }
}

/**
 * Editor ViewModel의 로컬 상태
 * 
 * UI에서 측정되는 값들을 담는 내부 상태입니다.
 * WidgetDocument는 Repository Flow에서 오고,
 * 이 값들은 UI 레이아웃 측정에서 옵니다.
 */
private data class LocalEditorState(
    val canvasBounds: Rect? = null,
    val layoutBounds: Rect? = null,
    val errorMessage: String? = null
)


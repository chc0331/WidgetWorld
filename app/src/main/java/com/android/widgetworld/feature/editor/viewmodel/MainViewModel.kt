package com.android.widgetworld.feature.editor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.widgetworld.domain.usecase.LoadWidgetDocumentUseCase
import com.android.widgetworld.proto.LayoutType
import com.android.widgetworld.proto.WidgetDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Main 화면의 UI 상태
 * 
 * State Hosting 원칙: 불변 data class
 * 
 * @property hasExistingWidget 기존 위젯 문서가 있는지 여부
 *                             (layout_type이 설정되었거나 컴포넌트가 있으면 true)
 * @property isLoading 문서 로딩 중 여부
 */
data class MainUiState(
    val hasExistingWidget: Boolean = false,
    val isLoading: Boolean = true
)

/**
 * Main 화면의 UI 이벤트
 * 
 * State Hosting 원칙: sealed interface
 * Composable에서 ViewModel로 전달되는 모든 사용자 액션
 */
sealed interface MainUiEvent {
    /**
     * "새 위젯 만들기" 버튼 클릭
     */
    data object OnCreateNewWidget : MainUiEvent
    
    /**
     * "편집 계속하기" 버튼 클릭
     */
    data object OnContinueEditing : MainUiEvent
}

/**
 * Main 화면 ViewModel
 * 
 * PRD 참조: 섹션 4-1 "Main 화면"
 * 
 * State Hosting 원칙 준수:
 * - UI State는 StateFlow로 노출
 * - UI Event는 handleEvent()로 처리
 * - State는 copy()로만 업데이트
 * - 단방향 데이터 흐름 보장
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val loadWidgetDocument: LoadWidgetDocumentUseCase
) : ViewModel() {
    
    /**
     * UI 상태
     * 
     * WidgetDocument를 관찰하여 기존 위젯 유무를 판단합니다.
     */
    val uiState: StateFlow<MainUiState> = loadWidgetDocument()
        .map { document ->
            MainUiState(
                hasExistingWidget = hasExistingWidget(document),
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState(isLoading = true)
        )
    
    /**
     * UI 이벤트 처리
     * 
     * Composable에서 이 메서드를 호출하여 사용자 액션을 전달합니다.
     * 
     * @param event 처리할 UI 이벤트
     */
    fun handleEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.OnCreateNewWidget -> {
                // Navigation은 Composable에서 처리
                // ViewModel은 상태 변경만 담당
                // (현재는 상태 변경 없이 Navigation만 필요)
            }
            MainUiEvent.OnContinueEditing -> {
                // Navigation은 Composable에서 처리
                // (현재는 상태 변경 없이 Navigation만 필요)
            }
        }
    }
    
    /**
     * 기존 위젯이 있는지 판단
     * 
     * Layout Type이 설정되었거나 UI 컴포넌트가 하나라도 있으면
     * 기존 위젯이 있다고 판단합니다.
     * 
     * @param document 현재 WidgetDocument
     * @return true: 기존 위젯 있음, false: 빈 문서
     */
    private fun hasExistingWidget(document: WidgetDocument): Boolean {
        return document.layoutType != LayoutType.LAYOUT_TYPE_UNSPECIFIED ||
                document.uiListCount > 0
    }
}


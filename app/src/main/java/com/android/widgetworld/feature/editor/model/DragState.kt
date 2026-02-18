package com.android.widgetworld.feature.editor.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset

/**
 * Drag & Drop 상태
 * 
 * PRD 참조: 섹션 4-3-1 "Drag State 정의 및 초기화"
 * 
 * Drag 중인 컴포넌트의 상태를 나타냅니다.
 * Long Press → Dragging → Drop 흐름에서 사용됩니다.
 * 
 * @property componentName 드래그 중인 컴포넌트 이름 (예: "Button", "Text")
 * @property isDragging Drag 중인지 여부
 * @property isDropped Drop이 완료되었는지 여부
 * @property windowOffset Window 기준 Drag 포지션
 * @property layoutOffset Layout 기준 Drag 포지션 (좌표 변환 후)
 * @property remoteComposeDoc RemoteCompose Document (ByteArray)
 * @property dragContent Drag 중 표시할 컨텐츠 (@Composable)
 */
data class DragState(
    val componentName: String,
    val isDragging: Boolean = false,
    val isDropped: Boolean = false,
    val windowOffset: Offset = Offset.Zero,
    val layoutOffset: Offset = Offset.Zero,
    val remoteComposeDoc: ByteArray,
    val dragContent: @Composable () -> Unit
) {
    // ByteArray는 equals/hashCode에서 참조 비교되므로 재정의
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DragState

        if (componentName != other.componentName) return false
        if (isDragging != other.isDragging) return false
        if (isDropped != other.isDropped) return false
        if (windowOffset != other.windowOffset) return false
        if (layoutOffset != other.layoutOffset) return false
        if (!remoteComposeDoc.contentEquals(other.remoteComposeDoc)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = componentName.hashCode()
        result = 31 * result + isDragging.hashCode()
        result = 31 * result + isDropped.hashCode()
        result = 31 * result + windowOffset.hashCode()
        result = 31 * result + layoutOffset.hashCode()
        result = 31 * result + remoteComposeDoc.contentHashCode()
        return result
    }
}


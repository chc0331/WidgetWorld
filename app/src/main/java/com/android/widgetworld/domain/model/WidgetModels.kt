package com.android.widgetworld.domain.model

import com.android.widgetworld.proto.LayoutType
import com.android.widgetworld.proto.Position
import com.android.widgetworld.proto.UiComponent
import com.android.widgetworld.proto.WidgetDocument
import com.android.widgetworld.proto.position
import com.android.widgetworld.proto.uiComponent
import com.android.widgetworld.proto.widgetDocument
import com.google.protobuf.ByteString
import java.util.UUID

/**
 * Domain Layer Wrapper for Proto Models
 * 
 * Proto 타입을 더욱 Kotlin-friendly하게 만들고,
 * 비즈니스 로직에서 사용하기 편한 헬퍼 함수들을 제공합니다.
 */

// ============================================================================
// Extension Functions for Proto Types
// ============================================================================

/**
 * WidgetDocument 확장 함수
 */

/**
 * 빈 WidgetDocument 생성
 */
fun emptyWidgetDocument(): WidgetDocument = widgetDocument {
    layoutType = LayoutType.LAYOUT_TYPE_UNSPECIFIED
}

/**
 * UI 컴포넌트 추가
 */
fun WidgetDocument.addComponent(component: UiComponent): WidgetDocument {
    return this.toBuilder()
        .addUiList(component)
        .build()
}

/**
 * UI 컴포넌트 제거 (id 기준)
 */
fun WidgetDocument.removeComponent(componentId: String): WidgetDocument {
    val filteredList = this.uiListList.filter { it.id != componentId }
    return this.toBuilder()
        .clearUiList()
        .addAllUiList(filteredList)
        .build()
}

/**
 * 레이아웃 타입 변경
 */
fun WidgetDocument.withLayoutType(layoutType: LayoutType): WidgetDocument {
    return this.toBuilder()
        .setLayoutType(layoutType)
        .build()
}

/**
 * 특정 ID의 컴포넌트 찾기
 */
fun WidgetDocument.findComponent(componentId: String): UiComponent? {
    return this.uiListList.find { it.id == componentId }
}

/**
 * UiComponent 확장 함수
 */

/**
 * Position 업데이트
 */
fun UiComponent.withPosition(x: Float, y: Float, width: Float, height: Float): UiComponent {
    return this.toBuilder()
        .setPosition(position {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        })
        .build()
}

/**
 * Position 업데이트 (Position 객체 사용)
 */
fun UiComponent.withPosition(newPosition: Position): UiComponent {
    return this.toBuilder()
        .setPosition(newPosition)
        .build()
}

/**
 * Content 업데이트
 */
fun UiComponent.withContent(content: ByteArray): UiComponent {
    return this.toBuilder()
        .setContent(ByteString.copyFrom(content))
        .build()
}

/**
 * Position 확장 함수
 */

/**
 * Position이 다른 Position 내부에 있는지 확인
 */
fun Position.isInside(bounds: Position): Boolean {
    return this.x >= bounds.x &&
            this.y >= bounds.y &&
            this.x + this.width <= bounds.x + bounds.width &&
            this.y + this.height <= bounds.y + bounds.height
}

/**
 * Position 이동
 */
fun Position.translate(dx: Float, dy: Float): Position {
    return position {
        this.x = this@translate.x + dx
        this.y = this@translate.y + dy
        this.width = this@translate.width
        this.height = this@translate.height
    }
}

/**
 * Position 크기 조정
 */
fun Position.resize(newWidth: Float, newHeight: Float): Position {
    return position {
        this.x = this@resize.x
        this.y = this@resize.y
        this.width = newWidth
        this.height = newHeight
    }
}

// ============================================================================
// Builder Helper Functions
// ============================================================================

/**
 * UiComponent 빌더 헬퍼
 * 
 * @param name 컴포넌트 이름/타입
 * @param x Layout 기준 x 좌표
 * @param y Layout 기준 y 좌표
 * @param width 너비
 * @param height 높이
 * @param content RemoteCompose Document (ByteArray)
 * @param id 고유 식별자 (기본값: 자동 생성 UUID)
 */
fun createUiComponent(
    name: String,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    content: ByteArray,
    id: String = UUID.randomUUID().toString()
): UiComponent = uiComponent {
    this.id = id
    this.name = name
    this.position = position {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }
    this.content = ByteString.copyFrom(content)
}

/**
 * Position 빌더 헬퍼
 */
fun createPosition(
    x: Float,
    y: Float,
    width: Float,
    height: Float
): Position = position {
    this.x = x
    this.y = y
    this.width = width
    this.height = height
}

// ============================================================================
// Type Aliases for Readability
// ============================================================================

/**
 * RemoteCompose Document를 명확하게 표현하기 위한 타입 별칭
 */
typealias RemoteComposeDocument = ByteArray

/**
 * Component ID를 명확하게 표현하기 위한 타입 별칭
 */
typealias ComponentId = String


package com.android.widgetworld.core.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.widgetworld.proto.LayoutType

/**
 * Layout 크기 정의 (Single Source of Truth)
 * 
 * PRD 참조: 섹션 4-2 "Layout 컴포넌트 선택"
 * 
 * Layout 타입별 크기를 정의합니다.
 * 모든 컴포넌트에서 이 객체를 참조하여 일관된 크기를 사용합니다.
 * 
 * Layout 크기 (MVP):
 * - MEDIUM: 200dp × 120dp
 * - LARGE: 280dp × 180dp
 * - FULL: 360dp × 240dp
 * 
 * 실제 Android 위젯 크기는 추후 적용 (섹션 5)
 */
object LayoutDimensions {
    
    /**
     * Layout 타입에 해당하는 크기를 반환합니다.
     * 
     * @param layoutType Layout 타입
     * @return (width, height) Pair, 타입이 UNSPECIFIED면 (0dp, 0dp)
     */
    fun getSize(layoutType: LayoutType): Pair<Dp, Dp> = when (layoutType) {
        LayoutType.MEDIUM -> 200.dp to 120.dp
        LayoutType.LARGE -> 280.dp to 180.dp
        LayoutType.FULL -> 360.dp to 240.dp
        else -> 0.dp to 0.dp
    }
    
    /**
     * Layout 너비 반환
     */
    fun getWidth(layoutType: LayoutType): Dp = getSize(layoutType).first
    
    /**
     * Layout 높이 반환
     */
    fun getHeight(layoutType: LayoutType): Dp = getSize(layoutType).second
}


package com.android.widgetworld.domain.model

import com.android.widgetworld.proto.WidgetDocument

/**
 * 위젯 문서 히스토리 아이템 (향후 구현용)
 * 
 * Room에 저장될 히스토리 항목의 메타데이터를 표현합니다.
 * 실제 구현은 향후 섹션(하이브리드 저장소)에서 진행됩니다.
 * 
 * 사용 시나리오:
 * - 히스토리 목록 화면에서 표시
 * - 특정 버전으로 복원
 * - 썸네일과 함께 미리보기 제공
 * 
 * 향후 확장:
 * - Room Entity로 변환하여 저장
 * - 썸네일 이미지 자동 생성
 * - 변경 차이(Diff) 정보 추가 가능
 * 
 * @property id 히스토리 고유 ID (UUID)
 * @property document 저장된 WidgetDocument (Proto)
 * @property thumbnailPath 썸네일 이미지 파일 경로 (nullable)
 * @property createdAt 생성 시간 (Unix timestamp)
 * @property updatedAt 수정 시간 (Unix timestamp)
 */
data class WidgetDocumentHistoryItem(
    val id: String,
    val document: WidgetDocument,
    val thumbnailPath: String?,
    val createdAt: Long,
    val updatedAt: Long
)


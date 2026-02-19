package com.android.widgetworld.core.model

/**
 * MVP용 샘플 컴포넌트 정의
 * 
 * PRD 참조: 섹션 4-3-0 "컴포넌트 팔레트/리스트"
 * 
 * 실제 RemoteCompose Document는 섹션 5에서 구현됩니다.
 * MVP 단계에서는 간단한 Mock 데이터를 사용합니다.
 */
object SampleComponents {
    
    /**
     * 샘플 컴포넌트 아이템
     * 
     * @property name 컴포넌트 이름
     * @property description 컴포넌트 설명
     * @property emoji 컴포넌트를 나타내는 이모지
     * @property remoteComposeDoc RemoteCompose Document (MVP: Mock ByteArray)
     */
    data class ComponentItem(
        val name: String,
        val description: String,
        val emoji: String,
        val remoteComposeDoc: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ComponentItem

            if (name != other.name) return false
            if (description != other.description) return false
            if (emoji != other.emoji) return false
            if (!remoteComposeDoc.contentEquals(other.remoteComposeDoc)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + emoji.hashCode()
            result = 31 * result + remoteComposeDoc.contentHashCode()
            return result
        }
    }
    
    /**
     * 사용 가능한 컴포넌트 목록 (MVP)
     * 
     * 실제 RemoteCompose Document는 추후 구현합니다.
     * 현재는 컴포넌트 이름을 ByteArray로 변환하여 사용합니다.
     * 
     * TODO: Future Enhancement - Add 2-Depth Category Structure
     * 
     * Planned:
     * - data class ComponentCategory(id, name, emoji, components: List<ComponentItem>)
     * - ComponentItem to include: id, categoryId fields
     * 
     * Example Structure:
     * ```
     * ComponentCategory(
     *   id = "button",
     *   name = "Button",
     *   emoji = "🔘",
     *   components = listOf(
     *     ComponentItem(id = "icon_button", categoryId = "button", name = "IconButton", ...),
     *     ComponentItem(id = "image_button", categoryId = "button", name = "ImageButton", ...),
     *     ComponentItem(id = "animation_button", categoryId = "button", name = "AnimationButton", ...)
     *   )
     * )
     * ```
     */
    val availableComponents = listOf(
        ComponentItem(
            name = "Button",
            description = "클릭 가능한 버튼",
            emoji = "🔘",
            remoteComposeDoc = "Button".toByteArray()
        ),
        ComponentItem(
            name = "Text",
            description = "텍스트 레이블",
            emoji = "📝",
            remoteComposeDoc = "Text".toByteArray()
        ),
        ComponentItem(
            name = "Image",
            description = "이미지 표시",
            emoji = "🖼️",
            remoteComposeDoc = "Image".toByteArray()
        )
    )
}


package com.android.widgetworld.domain.usecase

import com.android.widgetworld.domain.model.ComponentId
import com.android.widgetworld.domain.model.addComponent
import com.android.widgetworld.domain.model.createUiComponent
import com.android.widgetworld.domain.repository.WidgetRepository
import java.util.UUID
import javax.inject.Inject

/**
 * WidgetDocument에 UI 컴포넌트를 추가하는 UseCase
 * 
 * Drag&Drop 완료 후 새로운 컴포넌트를 문서에 추가합니다.
 * UUID를 자동 생성하여 고유 ID를 부여하고, 생성된 ID를 반환합니다.
 * 
 * PRD 참조: 섹션 5-3-3 "Drop Event → UiComponent 생성 및 문서 저장"
 * 
 * 사용 예시:
 * ```
 * viewModelScope.launch {
 *     val result = addUiComponent(
 *         AddComponentParams(
 *             name = "Button",
 *             x = layoutOffset.x,
 *             y = layoutOffset.y,
 *             width = 100f,
 *             height = 50f,
 *             content = byteArrayOf(...)
 *         )
 *     )
 *     result.onSuccess { componentId ->
 *         // 성공: 생성된 ID로 추가 작업 가능
 *     }.onFailure { exception ->
 *         // 에러 처리
 *     }
 * }
 * ```
 */
class AddUiComponentUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    /**
     * UI 컴포넌트를 추가합니다.
     * 
     * @param params 컴포넌트 추가에 필요한 파라미터
     * @return 성공 시 생성된 ComponentId, 실패 시 Exception
     */
    suspend operator fun invoke(params: AddComponentParams): Result<ComponentId> {
        val componentId = UUID.randomUUID().toString()
        
        val result = repository.updateWidgetDocument { currentDoc ->
            val newComponent = createUiComponent(
                id = componentId,
                name = params.name,
                x = params.x,
                y = params.y,
                width = params.width,
                height = params.height,
                content = params.content
            )
            currentDoc.addComponent(newComponent)
        }
        
        return result.map { componentId }
    }
}

/**
 * UI 컴포넌트 추가 파라미터
 * 
 * @property name 컴포넌트 타입명 (예: "Button", "Text", "Image")
 * @property x Layout 기준 x 좌표
 * @property y Layout 기준 y 좌표
 * @property width 컴포넌트 너비
 * @property height 컴포넌트 높이
 * @property content 컴포넌트 콘텐츠 (직렬화된 데이터, ByteArray)
 *                  Domain은 이 데이터가 무엇인지 알 필요 없음
 */
data class AddComponentParams(
    val name: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val content: ByteArray
) {
    // ByteArray는 equals/hashCode에서 참조 비교되므로 재정의
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddComponentParams

        if (name != other.name) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

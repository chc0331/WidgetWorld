package com.android.widgetworld.domain.usecase

import android.util.Log
import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.WidgetDocument
import javax.inject.Inject

/**
 * Debug용 UseCase - WidgetDocument 내용을 로그로 출력
 * 
 * Drop이 제대로 동작하는지 확인하기 위해 사용합니다.
 */
class GetWidgetDocumentDebugUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    suspend operator fun invoke(): WidgetDocument? {
        return repository.getWidgetDocument()
            .onSuccess { document ->
                Log.d("WidgetDocumentDebug", """
                    =====================================
                    WidgetDocument Debug Info
                    =====================================
                    Layout Type: ${document.layoutType}
                    Component Count: ${document.uiListCount}
                    
                    Components:
                    ${document.uiListList.mapIndexed { index, component ->
                        """
                        [$index] ${component.name}
                          - ID: ${component.id}
                          - Position: (${component.position.x}, ${component.position.y})
                          - Size: ${component.position.width} x ${component.position.height}
                          - Content: ${component.content.size()} bytes
                        """.trimIndent()
                    }.joinToString("\n")}
                    =====================================
                """.trimIndent())
            }
            .getOrNull()
    }
}


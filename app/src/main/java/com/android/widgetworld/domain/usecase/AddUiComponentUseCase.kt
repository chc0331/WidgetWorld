package com.android.widgetworld.domain.usecase

import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.UiComponent
import javax.inject.Inject

/**
 * UI 컴포넌트를 WidgetDocument에 추가하는 UseCase
 * 
 * PRD 참조: 섹션 4-3-3 "UiComponent 생성 및 문서 저장"
 * 
 * Drop 이벤트 발생 시 호출되며, 새로운 UI 컴포넌트를 생성하고
 * WidgetDocument의 ui_list에 추가한 후 DataStore에 저장합니다.
 * 
 * 사용 예시:
 * ```
 * val component = UiComponent.newBuilder()
 *     .setId(UUID.randomUUID().toString())
 *     .setName("Button")
 *     .setPosition(position)
 *     .setContent(remoteComposeDoc)
 *     .build()
 * 
 * addUiComponent(component)
 *     .onSuccess { /* 저장 성공 */ }
 *     .onFailure { exception -> /* 에러 처리 */ }
 * ```
 */
class AddUiComponentUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    /**
     * UI 컴포넌트를 현재 문서에 추가합니다.
     * 
     * @param component 추가할 UI 컴포넌트
     * @return Result<Unit> 성공/실패 결과
     */
    suspend operator fun invoke(component: UiComponent): Result<Unit> {
        return try {
            repository.addUiComponent(component)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

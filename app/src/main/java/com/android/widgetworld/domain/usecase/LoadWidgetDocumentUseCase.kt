package com.android.widgetworld.domain.usecase

import com.android.widgetworld.domain.repository.WidgetRepository
import com.android.widgetworld.proto.WidgetDocument
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * WidgetDocument를 반응형으로 로드하는 UseCase
 * 
 * Repository의 Flow를 그대로 반환하여 ViewModel이나 UI 레이어에서
 * 문서의 변경사항을 실시간으로 관찰할 수 있게 합니다.
 * 
 * 사용 예시:
 * ```
 * @HiltViewModel
 * class EditorViewModel @Inject constructor(
 *     private val loadWidgetDocument: LoadWidgetDocumentUseCase
 * ) : ViewModel() {
 *     val widgetDocument: StateFlow<WidgetDocument> = 
 *         loadWidgetDocument()
 *             .stateIn(viewModelScope, SharingStarted.Eagerly, emptyWidgetDocument())
 * }
 * ```
 */
class LoadWidgetDocumentUseCase @Inject constructor(
    private val repository: WidgetRepository
) {
    /**
     * WidgetDocument의 Flow를 반환합니다.
     * 
     * @return WidgetDocument의 Flow (문서가 변경될 때마다 emit)
     */
    operator fun invoke(): Flow<WidgetDocument> {
        return repository.observeWidgetDocument()
    }
}

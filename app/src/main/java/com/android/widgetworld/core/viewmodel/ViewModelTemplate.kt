package com.android.widgetworld.core.viewmodel

/**
 * ViewModel Template Example
 * 
 * BaseViewModel을 사용하는 예제 패턴입니다.
 * 실제 Feature ViewModel 구현 시 이 패턴을 따라주세요.
 */

// 1. UI State 정의 (data class, 불변)
data class ExampleUiState(
    val isLoading: Boolean = false,
    val data: String = "",
    val errorMessage: String? = null
)

// 2. UI Event 정의 (sealed interface)
sealed interface ExampleUiEvent {
    data object OnLoadData : ExampleUiEvent
    data class OnTextChanged(val text: String) : ExampleUiEvent
    data object OnRetry : ExampleUiEvent
}

// 3. Side Effect 정의 (sealed interface)
sealed interface ExampleSideEffect {
    data class ShowToast(val message: String) : ExampleSideEffect
    data object NavigateBack : ExampleSideEffect
}

// 4. ViewModel 구현
// @HiltViewModel 어노테이션과 함께 실제 구현 시 사용
// class ExampleViewModel @Inject constructor(
//     private val useCase: ExampleUseCase
// ) : BaseViewModel<ExampleUiState, ExampleUiEvent, ExampleSideEffect>() {
//
//     override fun createInitialState() = ExampleUiState()
//
//     override fun handleEvent(event: ExampleUiEvent) {
//         when (event) {
//             is ExampleUiEvent.OnLoadData -> loadData()
//             is ExampleUiEvent.OnTextChanged -> updateText(event.text)
//             is ExampleUiEvent.OnRetry -> retry()
//         }
//     }
//
//     private fun loadData() {
//         updateState { copy(isLoading = true) }
//         viewModelScope.launch {
//             useCase.execute()
//                 .onSuccess { data ->
//                     updateState { copy(isLoading = false, data = data) }
//                 }
//                 .onFailure { error ->
//                     updateState { copy(isLoading = false, errorMessage = error.message) }
//                     sendSideEffect(ExampleSideEffect.ShowToast("Error: ${error.message}"))
//                 }
//         }
//     }
//
//     private fun updateText(text: String) {
//         updateState { copy(data = text) }
//     }
//
//     private fun retry() {
//         updateState { copy(errorMessage = null) }
//         loadData()
//     }
// }

// 5. Composable에서 사용
// @Composable
// fun ExampleScreen(
//     viewModel: ExampleViewModel = hiltViewModel()
// ) {
//     val uiState by viewModel.uiState.collectAsState()
//     val context = LocalContext.current
//     
//     // Side Effect 처리
//     LaunchedEffect(Unit) {
//         viewModel.sideEffect.collect { effect ->
//             when (effect) {
//                 is ExampleSideEffect.ShowToast -> {
//                     Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
//                 }
//                 is ExampleSideEffect.NavigateBack -> {
//                     // Navigate back
//                 }
//             }
//         }
//     }
//     
//     // UI 렌더링
//     Column {
//         if (uiState.isLoading) {
//             CircularProgressIndicator()
//         } else {
//             Text(uiState.data)
//         }
//         
//         Button(onClick = { viewModel.handleEvent(ExampleUiEvent.OnLoadData) }) {
//             Text("Load Data")
//         }
//     }
// }


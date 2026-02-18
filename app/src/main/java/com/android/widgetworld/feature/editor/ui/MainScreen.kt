package com.android.widgetworld.feature.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.widgetworld.feature.editor.viewmodel.MainUiEvent
import com.android.widgetworld.feature.editor.viewmodel.MainUiState
import com.android.widgetworld.feature.editor.viewmodel.MainViewModel

/**
 * Main 화면
 * 
 * PRD 참조: 섹션 4-1 "Main 화면"
 * 
 * 앱의 진입점으로, 새 위젯을 만들거나 기존 위젯을 편집할 수 있습니다.
 * 
 * State Hosting 원칙:
 * - collectAsState()로 State 구독
 * - Event는 ViewModel.handleEvent()로 전달
 * - Navigation 콜백으로 화면 전환
 * 
 * @param onNavigateToEditor Editor 화면으로 이동 콜백
 * @param modifier Modifier
 * @param viewModel MainViewModel (Hilt 자동 주입)
 */
@Composable
fun MainScreen(
    onNavigateToEditor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    MainScreenContent(
        uiState = uiState,
        onEvent = { event ->
            viewModel.handleEvent(event)
            // 모든 이벤트는 Editor로 이동
            onNavigateToEditor()
        },
        modifier = modifier
    )
}

/**
 * Main 화면 컨텐츠 (Stateless)
 * 
 * State와 Event를 받아 UI를 렌더링합니다.
 * 테스트 및 프리뷰에 유용합니다.
 */
@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 로고 또는 타이틀
        Text(
            text = "WidgetWorld",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "드래그 앤 드롭으로\n위젯을 디자인하세요",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // 로딩 상태
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            // 기존 위젯이 있으면 "편집 계속하기" 버튼 표시
            if (uiState.hasExistingWidget) {
                Button(
                    onClick = { onEvent(MainUiEvent.OnContinueEditing) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "편집 계속하기",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { onEvent(MainUiEvent.OnCreateNewWidget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "새 위젯 만들기",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                // 빈 문서면 "새 위젯 만들기"만 표시
                Button(
                    onClick = { onEvent(MainUiEvent.OnCreateNewWidget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "새 위젯 만들기",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 도움말 텍스트
        Text(
            text = "MVP 버전 - Drag & Drop 에디터",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


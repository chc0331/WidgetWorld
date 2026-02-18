package com.android.widgetworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.widgetworld.domain.usecase.LoadWidgetDocumentUseCase
import com.android.widgetworld.domain.usecase.SetLayoutTypeUseCase
import com.android.widgetworld.proto.LayoutType
import com.android.widgetworld.proto.WidgetDocument
import com.android.widgetworld.ui.theme.WidgetWorldTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainActivity - 섹션 3 데이터 흐름 검증용
 * 
 * 이 화면은 DataStore + Repository 구현이 정상 작동하는지 확인하기 위한
 * 임시 테스트 화면입니다.
 * 
 * 테스트 시나리오:
 * 1. 앱 최초 실행 → 빈 문서 (LAYOUT_TYPE_UNSPECIFIED, 0개 컴포넌트)
 * 2. 레이아웃 타입 변경 (MEDIUM/LARGE/FULL) → 즉시 저장
 * 3. 앱 종료 후 재시작 → 이전에 선택한 레이아웃 타입 복원 확인
 * 
 * 섹션 4에서 실제 Editor UI를 구현하면 이 테스트 코드는 제거됩니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val testViewModel: DataStoreTestViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetWorldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DataStoreTestScreen(
                        viewModel = testViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * 섹션 3 검증용 ViewModel
 * 
 * DataStore + Repository + UseCase 연동을 테스트합니다.
 */
@HiltViewModel
class DataStoreTestViewModel @Inject constructor(
    private val loadWidgetDocument: LoadWidgetDocumentUseCase,
    private val setLayoutType: SetLayoutTypeUseCase
) : ViewModel() {
    
    /**
     * WidgetDocument를 반응형으로 관찰
     * 
     * Repository의 Flow를 StateFlow로 변환하여 UI에 노출합니다.
     * 문서가 변경되면 자동으로 UI가 업데이트됩니다.
     */
    val widgetDocument: StateFlow<WidgetDocument?> = loadWidgetDocument()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    
    /**
     * 레이아웃 타입 변경 및 저장
     * 
     * SetLayoutTypeUseCase를 사용하여 레이아웃 타입을 변경합니다.
     * UseCase 내부에서 Repository를 통해 DataStore에 자동 저장됩니다.
     */
    fun changeLayoutType(layoutType: LayoutType) {
        viewModelScope.launch {
            setLayoutType(layoutType)
                .onSuccess {
                    // 저장 성공 (자동으로 widgetDocument Flow가 업데이트됨)
                }
                .onFailure { exception ->
                    // 에러 처리 (실제 앱에서는 Snackbar 등으로 표시)
                    exception.printStackTrace()
                }
        }
    }
}

/**
 * 섹션 3 검증용 테스트 화면
 * 
 * DataStore 저장/로드가 정상 작동하는지 확인하기 위한 UI입니다.
 */
@Composable
fun DataStoreTestScreen(
    viewModel: DataStoreTestViewModel,
    modifier: Modifier = Modifier
) {
    val document by viewModel.widgetDocument.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // 타이틀
        Text(
            text = "섹션 3: DataStore 테스트",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 현재 상태 표시
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "현재 저장된 문서",
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (document == null) {
                    Text("로딩 중...")
                } else {
                    Text("레이아웃 타입: ${document!!.layoutType.name}")
                    Text("컴포넌트 개수: ${document!!.uiListCount}")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 레이아웃 타입 변경 버튼들
        Text(
            text = "레이아웃 타입 선택",
            style = MaterialTheme.typography.titleMedium
        )
        
        Button(
            onClick = { viewModel.changeLayoutType(LayoutType.MEDIUM) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("MEDIUM으로 변경")
        }
        
        Button(
            onClick = { viewModel.changeLayoutType(LayoutType.LARGE) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LARGE로 변경")
        }
        
        Button(
            onClick = { viewModel.changeLayoutType(LayoutType.FULL) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("FULL로 변경")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 테스트 안내
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "테스트 방법",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "1. 레이아웃 타입을 선택하세요\n" +
                            "2. 앱을 종료하세요 (백그라운드가 아닌 완전 종료)\n" +
                            "3. 앱을 다시 실행하세요\n" +
                            "4. 이전에 선택한 레이아웃이 복원되면 성공!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
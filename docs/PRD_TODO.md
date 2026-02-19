# WidgetWorld PRD 기반 개발 Todo (MVP)

> Source: Notion `WidgetWorld PRD (Product Requirements Document)`  
> Goal: 사용자가 Drag&Drop으로 Android 위젯 UI를 직접 설계하는 앱

## 0) 범위/원칙 (PRD Guidance)
- [x] MVP 목표 재확인: Drag&Drop 기반 위젯 UI 에디터 + 위젯 렌더링 연결
- [x] Tech Stack 고정
  - [x] Platform: Android
  - [x] Language: Kotlin
  - [x] UI toolkit: Jetpack Compose (MaterialTheme 준수)
  - [x] Widget UI: RemoteCompose
  - [x] Architecture: MVVM + Clean Architecture (과용 금지), State Hosting 준수
  - [x] State Management: ViewModel + StateFlow
  - [x] DI: Hilt
  - [x] Storage: DataStore (Proto) (추후 Room 확장 가능)
- [x] DO
  - [x] Compose로 화면 구현, Google Material 디자인 가이드 준수
  - [x] 상태는 ViewModel 중심 (State Hosting 준수)
  - [x] UI State가 비대해질 시 ViewModel 분리
  - [x] 구조 단순 유지, MVP 중심
  - [x] 필요 라이브러리 미리 셋업
- [x] DO NOT
  - [x] 렌더링 엔진 과도하게 구현하지 않기
  - [x] 복잡한 Clean Architecture 과용하지 않기
  - [x] MVP 범위 외 기능 추가하지 않기

## 1) 프로젝트 기본 셋업 (Android/Compose/MVVM)
- [x] 모듈/패키지 구조 설계 (최소 단위로)
  - [x] `core` 공통 유틸/타입
  - [x] `domain` 유스케이스/도메인 모델(Proto wrapper 포함)
  - [x] `data` DataStore(Proto)/Repository
  - [x] `feature_editor` (Main/WidgetCanvas/DnD)
  - [x] `feature_widget` (위젯 렌더링/연동)
- [x] Hilt 셋업
  - [x] Application 클래스 생성 및 Hilt 설정
  - [x] DataStore/Repository/UseCase DI 모듈 구성
- [x] ViewModel + StateFlow 패턴 가이드 확정
  - [ ] (섹션 4에서 구현) State Hosting 원칙 준수
    - [ ] UI State: data class, ViewModel에서만 변경
    - [ ] UI Event: sealed interface, Composable → ViewModel
    - [ ] Side Effect: Channel, 일회성 이벤트
    - [ ] 단방향 데이터 흐름: Event → ViewModel → State → UI

## 2) Core Domain Model (PRD Proto) 구현
### 2-1) Proto 스키마 정의
- [x] `LayoutType`
  - [x] `MEDIUM = 1`
  - [x] `LARGE = 2`
  - [x] `FULL = 3`
- [x] `WidgetDocument`
  - [x] `LayoutType layout_type = 1`
  - [x] `repeated UiComponent ui_list = 2`
- [x] `UiComponent`
  - [x] `string id = 1`
  - [x] `string name = 2`
  - [x] `Position position = 3`
  - [x] `bytes content = 4` (RemoteCompose Document ByteArray)
- [x] `Position`
  - [x] `float x = 1`
  - [x] `float y = 2`
  - [x] `float width = 3`
  - [x] `float height = 4`

### 2-2) Proto 생성/빌드 파이프라인
- [x] protobuf 설정 및 Kotlin 코드 생성 확인
- [x] 생성된 클래스/패키지 네이밍 점검

### 2-3) 최소 UseCase/Policy 정의
- [x] `LoadWidgetDocument`
- [x] `SaveWidgetDocument`
- [x] `SetLayoutType`
- [x] `AddUiComponent`
- [x] `ValidateDropPosition` (레이아웃 영역 drop 가능 여부)
- [x] `ConvertWindowToLayoutOffset` (좌표 변환 정책)

## 3) Storage: DataStore(Proto)
- [x] Proto DataStore 구현
  - [x] WidgetDocumentSerializer 구현 (Proto parsing/writing)
  - [x] WidgetDataStore 클래스 구현 (DataStore wrapper)
  - [x] 파일명: `widget_document.pb` (앱 내부 저장소)
  - [x] 기본값: 빈 `WidgetDocument` 제공
  - [x] 읽기/쓰기/에러 처리 (IOException, CorruptionException)
- [x] DataSource 추상화 레이어 추가 (Room 확장 대비)
  - [x] WidgetDocumentLocalDataSource 인터페이스 정의
  - [x] WidgetDataStoreSource 구현 (DataStore 기반)
- [x] Repository 구현
  - [x] WidgetRepositoryImpl 작성 (WidgetRepository 인터페이스 구현)
  - [x] `observeWidgetDocument(): Flow<WidgetDocument>` 구현
  - [x] `updateWidgetDocument(transform)` 형태로 원자적 업데이트 구현
  - [x] `getWidgetDocument(): Result<WidgetDocument>` 구현 (일회성 조회)
  - [x] 모든 예외를 Result로 래핑하여 Domain 레이어에 전달
  - [x] drop 직후 자동 저장 타이밍 확정
- [x] Hilt DI 모듈 구성
  - [x] DataModule에 DataStore 제공 메서드 추가
  - [x] DataModule에 LocalDataSource 바인딩 추가
  - [x] DataModule에 Repository 바인딩 추가
- [x] 데이터 흐름 검증
  - [x] 테스트 화면 구현 (MainActivity)
  - [x] 초기 로드 테스트 (빈 문서)
  - [x] 저장 및 앱 재시작 후 복원 테스트 준비 완료
- [x] 향후 Room 확장 고려사항 (MVP는 DataStore만 사용)
  - [x] LocalDataSource 추상화 레이어 구현 완료
  - [x] Repository 인터페이스는 현재대로 유지 (변경 없음)
  - [x] 향후 여러 위젯 관리 시: DataModule의 bindLocalDataSource만 변경
  - [x] 추가 Repository 생성 가능: `WidgetListRepository` (목록 관리용)
  - [x] DataStore 직접 의존 회피, 주입받아 사용 (LocalDataSource 추상화 완료)

### 향후 확장: 하이브리드 저장소 (섹션 6 또는 별도)
- [x] 인터페이스 설계 완료
  - [x] `WidgetDocumentHistoryItem` 도메인 모델 정의
  - [x] Repository에 히스토리 메서드 시그니처 추가 (undo/redo/observeHistory/restore)
  - [x] 기본 구현 제공 (NotImplementedError 또는 빈 Flow)
- [ ] Room Database 설계 및 구현
  - [ ] WidgetDocumentEntity 정의
  - [ ] WidgetDocumentDao 정의 (CRUD + 히스토리 조회)
  - [ ] WidgetWorldDatabase 생성
  - [ ] Migration 전략 수립
- [ ] 하이브리드 DataSource 구현
  - [ ] WidgetRoomSource (Room 기반 LocalDataSource 구현)
  - [ ] InMemoryHistorySource (Undo/Redo 스택, 최근 50개)
  - [ ] DataStoreSyncer (DataStore ↔ Room 백그라운드 동기화)
- [ ] Repository 하이브리드 로직 구현
  - [ ] updateWidgetDocument에 Undo 스택 푸시 연동
  - [ ] undo/redo 메서드 실제 구현 (메모리 스택 사용)
  - [ ] 백그라운드 Room 동기화 (debounce 적용)
  - [ ] 초기 로드 시 DataStore ↔ Room 동기화 검증
- [ ] 히스토리 UseCase 추가
  - [ ] UndoWidgetDocumentUseCase
  - [ ] RedoWidgetDocumentUseCase
  - [ ] LoadHistoryListUseCase
  - [ ] RestoreFromHistoryUseCase
  - [ ] SaveCurrentAsHistoryUseCase (수동 저장)
- [ ] 썸네일 생성 및 관리
  - [ ] Canvas 스크린샷 캡처 유틸
  - [ ] 썸네일 이미지 파일 저장
  - [ ] 썸네일 경로를 히스토리에 연결
- [ ] 히스토리 UI 구현
  - [ ] Undo/Redo 버튼 (Editor 화면)
  - [ ] 히스토리 목록 화면 (썸네일 + 타임스탬프)
  - [ ] 히스토리 복원 다이얼로그

**하이브리드 저장소 아키텍처 전략**:
- **DataStore**: 현재 작업 중인 문서 캐시 (빠른 읽기/쓰기, UI 즉시 반영)
- **Room**: 영속 저장소 + 히스토리 관리 (썸네일 포함, 검색/필터 가능)
- **Memory Stack**: Undo/Redo 스택 (최근 50개, 메모리 내에서 즉시 처리)
- **Syncer**: DataStore ↔ Room 백그라운드 동기화 (debounce 1초)

**하이브리드 데이터 흐름**:
1. **편집 작업**: Memory Stack 푸시 → DataStore 즉시 저장 → UI 즉시 반영 → Room 백그라운드 동기화
2. **Undo/Redo**: Memory Stack pop/push → DataStore 복원 → UI 즉시 반영
3. **앱 시작**: DataStore에서 로드 (빠름) → 백그라운드로 Room과 동기화 검증
4. **히스토리 조회**: Room에서 목록 로드 (썸네일 포함)
5. **히스토리 복원**: Room에서 특정 버전 로드 → DataStore 업데이트 → UI 반영

## 4) UX Flow (PRD 5)

### 4-1) Main 화면 (PRD 5-1)
- [x] Main 화면 UI 구성
  - [x] 편집 진입 CTA (새 위젯/편집)
  - [x] 에디터 화면으로 네비게이션 연결
- [x] Main 화면 ViewModel 구현 (State Hosting 원칙 준수)
  - [x] UI State 정의 (data class, 불변)
  - [x] UI Event 정의 (sealed interface)
  - [x] Side Effect 정의 (sealed interface, 필요시)
  - [x] ViewModel 구현 (@HiltViewModel)
    - [x] StateFlow로 State 노출
    - [x] handleEvent() 메서드로 이벤트 처리
    - [x] State는 copy()로만 업데이트
  - [x] Composable 연결
    - [x] collectAsState()로 State 구독
    - [x] Event 발행은 ViewModel.handleEvent() 호출
    - [x] LaunchedEffect로 Side Effect 처리

### 4-2) Layout 컴포넌트 선택 → WidgetCanvas 배치 (PRD 5-2)
- [x] Layout Tab/UI 구성
  - [x] LayoutType 선택 UI 제공 (MEDIUM/LARGE/FULL)
  - [x] Layout 클릭 시 Canvas에 Layout 컨테이너 추가
  - [x] 접기/펼치기 기능 추가 (Canvas 시인성 향상)
- [x] WidgetCanvas(컨테이너) 구현
  - [x] Layout 영역(드롭 가능)과 Layout 밖 영역(드롭 불가) 시각적으로 구분
  - [x] Layout 추가 시 `WidgetDocument.layout_type` 업데이트
  - [x] "컨테이너 역할" 가이드(빈 상태) 제공

### 4-3) UI Component를 Layout 영역에 Drag&Drop으로 추가 (PRD 5-3)
- [x] Editor(Canvas + DnD) ViewModel 구현 (State Hosting 원칙 준수)
  - [x] UI State 정의 (data class, 불변)
    - [x] `widgetDocument: WidgetDocument`
    - [x] `dragState: DragState?` (Dragging 중인 상태)
    - [x] `canvasBounds: Rect?`
    - [x] `layoutBounds: Rect?`
  - [x] UI Event 정의 (sealed interface)
    - [x] `OnLayoutTypeSelected(layoutType: LayoutType)`
    - [x] `OnComponentLongPress(component, remoteComposeDoc)`
    - [x] ~~`OnDragPositionChanged(windowOffset, layoutOffset)`~~ (로컬 state로 최적화)
    - [ ] `OnDrop(layoutOffset, remoteComposeDoc)` (4-3-3에서 구현 예정)
  - [x] Side Effect 정의 (sealed interface, 필요시)
  - [x] ViewModel 구현 (@HiltViewModel)
    - [x] StateFlow로 State 노출
    - [x] handleEvent() 메서드로 이벤트 처리
    - [x] State는 copy()로만 업데이트
    - [x] Repository와 연동하여 WidgetDocument 저장/로드
  - [x] Composable 연결
    - [x] collectAsState()로 State 구독
    - [x] Event 발행은 ViewModel.handleEvent() 호출
    - [x] LaunchedEffect로 Side Effect 처리

#### 4-3-0) 컴포넌트 팔레트/리스트
- [x] UI Component 목록 UI 구성
  - [x] 각 항목 long press 가능 처리
  - [x] 항목별 RemoteCompose Document(ByteArray) 준비/획득 방식 정의
  - [x] 접기/펼치기 기능 추가 (Canvas 시인성 향상)
  - [x] TODO: 2-depth 구조 주석 추가 (향후 개선 예정)

#### 4-3-1) Long Press Event (PRD 5-3-1)
- [x] Long press 시 RemoteCompose Document(ByteArray) 추출 (PRD 5-3-1-1)
- [x] Drag 준비: Drag State 정의 및 초기화 (PRD 5-3-1-2)
  - [x] Drag State 필드 정의 (PRD 명세)
    - [x] Drag 중인지 여부 `isDragging: Boolean`
    - [x] Drop 여부 `isDropped: Boolean`
    - [x] Window 기준 Drag 포지션 `windowOffset: Offset`
    - [x] Layout 기준 Drag 포지션 `layoutOffset: Offset`
    - [x] RemoteCompose Document `remoteComposeDoc: ByteArray`
    - [x] Drag 컨텐츠 `dragContent: @Composable () -> Unit`

#### 4-3-2) Dragging Event (PRD 5-3-2)
- [x] Window 기준 x,y 좌표 업데이트 (PRD 5-3-2-1)
  - [x] 현재 x,y가 Widget Canvas 영역 내인지 체크 로직 구현 (PRD bullet)
    - [x] Canvas bounds 계산/갱신(리컴포지션/레이아웃 변화 대응)
    - [x] Layout bounds 계산/갱신
    - [x] in/out 판정 결과를 Drag State에 반영
  - [x] Drop이 될 수 있는 영역을 미리 표시 (PRD bullet)
    - [x] Layout 영역 하이라이트 (배경색 변경)
    - [x] Drop 불가 시(레이아웃 밖) 경고 표시 ("✗ 배치 불가")
- [x] Layout 기준 x,y 좌표 업데이트 (PRD 5-3-2-2)
  - [x] Window → Layout 좌표 변환 함수 구현 (ConvertWindowToLayoutOffsetUseCase)
- [x] Drag 중 시각 효과 (PRD 5-3-2-3)
  - [x] Drag 컨텐츠 표시 (DragOverlay 구현)
  - [x] 성능 최적화 (rememberDragStateHandler로 로컬 state 관리)
  - [ ] Layout 내부에 Drop 위치 프리뷰 표시 (향후 개선)

#### 4-3-3) Drop Event (PRD 5-3-3)
- [x] Layout 영역 Drop 가능 여부 최종 체크 (PRD 5-3-3-1)
- [x] Window 기준 x,y 좌표 기준으로 Drop 처리 (PRD 5-3-3-2)
- [x] UiComponent 생성 및 문서 저장 (PRD 5-3-3-3)
  - [x] `UiComponent` 생성 규칙 결정
    - [x] `id` 생성 (UUID)
    - [x] `name` 매핑 (컴포넌트 타입명)
  - [x] `Position` 채우기
    - [x] `x`,`y`: Layout 기준 좌표
    - [x] `width`,`height`: 초기값 50dp (MVP 고정값)
  - [x] `content = remoteComposeDoc(ByteArray)` 저장
  - [x] `WidgetDocument.ui_list`에 append (AddUiComponentUseCase)
  - [x] DataStore 저장 트리거 (Repository 자동 처리)

### 4-4) UI Component를 Layout 영역 밖에 Drag&Drop (PRD 5-4)
- [x] Layout 밖 Drop 불가 처리 (PRD 5-4-1)
  - [x] ValidateDropPositionUseCase 구현
  - [x] Drop 가능 여부 실시간 검증 (isValidDropPosition)
  - [x] 레이아웃 외 영역 경고 표시 (에러 배경색, "✗ 배치 불가")
  - [x] Drop 시 데이터 업데이트 금지 (handleDrop에서 검증)

## 5) 위젯 렌더링 (PRD TODO)
- [ ] 위젯 렌더링 Composable 구현부 작성
  - [ ] `WidgetDocument` → 실제 렌더링 트리 구성
  - [ ] `layout_type`에 따른 레이아웃/스케일 정책 적용
  - [ ] `ui_list` 순회하여 `position` 기반 배치
  - [ ] `UiComponent.content(ByteArray)`를 RemoteCompose로 렌더링하는 어댑터 구현
- [ ] 에디터 화면(Canvas)과 위젯 렌더링을 동일 데이터(WidgetDocument)로 연결
  - [ ] StateFlow 변경 즉시 프리뷰 반영

## 6) 최소 품질/테스트(필수만)
- [ ] 좌표/영역 체크 테스트
  - [ ] Layout 밖 Drop 불가 케이스
  - [ ] Window→Layout 좌표 변환 케이스
- [ ] Proto DataStore 저장/복원 테스트
  - [ ] `ui_list` 추가 후 재실행 시 복원



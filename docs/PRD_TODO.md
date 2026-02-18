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

## 4) UX Flow (PRD 5)

### 4-1) Main 화면 (PRD 5-1)
- [ ] Main 화면 UI 구성
  - [ ] 편집 진입 CTA (새 위젯/편집)
  - [ ] 에디터 화면으로 네비게이션 연결
- [ ] Main 화면 ViewModel 구현 (State Hosting 원칙 준수)
  - [ ] UI State 정의 (data class, 불변)
  - [ ] UI Event 정의 (sealed interface)
  - [ ] Side Effect 정의 (sealed interface, 필요시)
  - [ ] ViewModel 구현 (@HiltViewModel)
    - [ ] StateFlow로 State 노출
    - [ ] handleEvent() 메서드로 이벤트 처리
    - [ ] State는 copy()로만 업데이트
  - [ ] Composable 연결
    - [ ] collectAsState()로 State 구독
    - [ ] Event 발행은 ViewModel.handleEvent() 호출
    - [ ] LaunchedEffect로 Side Effect 처리

### 4-2) Layout 컴포넌트 선택 → WidgetCanvas 배치 (PRD 5-2)
- [ ] Layout Tab/UI 구성
  - [ ] LayoutType 선택 UI 제공 (MEDIUM/LARGE/FULL)
  - [ ] Layout 클릭 시 Canvas에 Layout 컨테이너 추가
- [ ] WidgetCanvas(컨테이너) 구현
  - [ ] Layout 영역(드롭 가능)과 Layout 밖 영역(드롭 불가) 시각적으로 구분
  - [ ] Layout 추가 시 `WidgetDocument.layout_type` 업데이트
  - [ ] "컨테이너 역할" 가이드(빈 상태) 제공

### 4-3) UI Component를 Layout 영역에 Drag&Drop으로 추가 (PRD 5-3)
- [ ] Editor(Canvas + DnD) ViewModel 구현 (State Hosting 원칙 준수)
  - [ ] UI State 정의 (data class, 불변)
    - [ ] `widgetDocument: WidgetDocument`
    - [ ] `dragState: DragState?` (Dragging 중인 상태)
    - [ ] `canvasBounds: Rect?`
    - [ ] `layoutBounds: Rect?`
  - [ ] UI Event 정의 (sealed interface)
    - [ ] `OnLayoutTypeSelected(layoutType: LayoutType)`
    - [ ] `OnComponentLongPress(component, remoteComposeDoc)`
    - [ ] `OnDragPositionChanged(windowOffset, layoutOffset)`
    - [ ] `OnDrop(layoutOffset, remoteComposeDoc)`
  - [ ] Side Effect 정의 (sealed interface, 필요시)
  - [ ] ViewModel 구현 (@HiltViewModel)
    - [ ] StateFlow로 State 노출
    - [ ] handleEvent() 메서드로 이벤트 처리
    - [ ] State는 copy()로만 업데이트
    - [ ] Repository와 연동하여 WidgetDocument 저장/로드
  - [ ] Composable 연결
    - [ ] collectAsState()로 State 구독
    - [ ] Event 발행은 ViewModel.handleEvent() 호출
    - [ ] LaunchedEffect로 Side Effect 처리

#### 4-3-0) 컴포넌트 팔레트/리스트
- [ ] UI Component 목록 UI 구성
  - [ ] 각 항목 long press 가능 처리
  - [ ] 항목별 RemoteCompose Document(ByteArray) 준비/획득 방식 정의

#### 4-3-1) Long Press Event (PRD 5-3-1)
- [ ] Long press 시 RemoteCompose Document(ByteArray) 추출 (PRD 5-3-1-1)
- [ ] Drag 준비: Drag State 정의 및 초기화 (PRD 5-3-1-2)
  - [ ] Drag State 필드 정의 (PRD 명세)
    - [ ] Drag 중인지 여부 `isDragging: Boolean`
    - [ ] Drop 여부 `isDropped: Boolean`
    - [ ] Window 기준 Drag 포지션 `windowOffset: Offset`
    - [ ] Layout 기준 Drag 포지션 `layoutOffset: Offset`
    - [ ] RemoteCompose Document `remoteComposeDoc: ByteArray`
    - [ ] Drag 컨텐츠 `dragContent: @Composable () -> Unit`

#### 4-3-2) Dragging Event (PRD 5-3-2)
- [ ] Window 기준 x,y 좌표 업데이트 (PRD 5-3-2-1)
  - [ ] 현재 x,y가 Widget Canvas 영역 내인지 체크 로직 구현 (PRD bullet)
    - [ ] Canvas bounds 계산/갱신(리컴포지션/레이아웃 변화 대응)
    - [ ] Layout bounds 계산/갱신
    - [ ] in/out 판정 결과를 Drag State에 반영
  - [ ] Drop이 될 수 있는 영역을 미리 표시 (PRD bullet)
    - [ ] Layout 영역 하이라이트
    - [ ] Drop 불가 시(레이아웃 밖) 경고 표시 정책과 연동
- [ ] Layout 기준 x,y 좌표 업데이트 (PRD 5-3-2-2)
  - [ ] Window → Layout 좌표 변환 함수 구현
- [ ] Drag 중 시각 효과 (PRD 5-3-2-3)
  - [ ] Drag 컨텐츠(원본) 투명 처리
  - [ ] Layout 내부에 Drop 위치 프리뷰 표시

#### 4-3-3) Drop Event (PRD 5-3-3)
- [ ] Layout 영역 Drop 가능 여부 최종 체크 (PRD 5-3-3-1)
- [ ] Window 기준 x,y 좌표 기준으로 Drop 처리 (PRD 5-3-3-2)
- [ ] UiComponent 생성 및 문서 저장 (PRD 5-3-3-3)
  - [ ] `UiComponent` 생성 규칙 결정
    - [ ] `id` 생성 (UUID 등)
    - [ ] `name` 매핑 (컴포넌트 타입명 등)
  - [ ] `Position` 채우기
    - [ ] `x`,`y`: Layout 기준 좌표
    - [ ] `width`,`height`: 초기값 정책(컴포넌트별/기본값) 결정
  - [ ] `content = remoteComposeDoc(ByteArray)` 저장
  - [ ] `WidgetDocument.ui_list`에 append
  - [ ] DataStore 저장 트리거 (drop 직후)

### 4-4) UI Component를 Layout 영역 밖에 Drag&Drop (PRD 5-4)
- [ ] Layout 밖 Drop 불가 처리 (PRD 5-4-1)
  - [ ] Drop 시 데이터 업데이트 금지
  - [ ] 레이아웃 외 영역에 붉은색 배경 경고 표시 (PRD bullet)

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



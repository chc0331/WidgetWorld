# BaseViewModel 정리 완료

## ✅ 완료된 작업

### 1. 파일 삭제
- ✅ `core/viewmodel/BaseViewModel.kt` 삭제
- ✅ `core/viewmodel/ViewModelTemplate.kt` 삭제

**이유**: YAGNI (You Aren't Gonna Need It) 원칙 적용
- 실제로 사용하지 않는 boilerplate 코드 제거
- 섹션 4(UX Flow 구현) 시점에 실제 필요에 맞춰 구현하는 것이 더 실용적

### 2. PRD_TODO.md 업데이트

#### 섹션 1-3 수정
**변경 전:**
```markdown
- [x] ViewModel + StateFlow 패턴 템플릿 확정
  - [x] UI State / UI Event / SideEffect 구분 기준 정리
  - [x] State Hosting 원칙 적용
```

**변경 후:**
```markdown
- [x] ViewModel + StateFlow 패턴 가이드 확정
  - [ ] (섹션 4에서 구현) State Hosting 원칙 준수
    - [ ] UI State: data class, ViewModel에서만 변경
    - [ ] UI Event: sealed interface, Composable → ViewModel
    - [ ] Side Effect: Channel, 일회성 이벤트
    - [ ] 단방향 데이터 흐름: Event → ViewModel → State → UI
```

#### 섹션 4에 ViewModel 구현 체크리스트 추가

**4-1) Main 화면**
- ViewModel 구현 체크리스트 추가 (State/Event/Effect 정의, Composable 연결)

**4-3) Editor (Canvas + DnD)**
- ViewModel 구현 체크리스트 추가 (DragState 포함한 상세 State 정의)
- Event 종류 명시 (OnLayoutTypeSelected, OnDragPositionChanged, OnDrop 등)

### 3. SECTION_0-1_COMPLETE.md 업데이트

**변경 내용:**
- "ViewModel 템플릿 작성" → "State Hosting 원칙 문서화"
- 실제 구현은 섹션 4에서 진행한다는 점 명시
- State Hosting 패턴 예제 코드 업데이트 (BaseViewModel 없이 직접 구현)

## 🎯 효과

### YAGNI 원칙 준수
- 미리 만든 추상화 제거
- 실제 필요할 때 구체적 요구사항에 맞춰 구현

### 명확한 가이드라인
- State Hosting 원칙은 TODO에 명확히 기록
- 각 화면 구현 시 체크리스트로 확인 가능

### 실용적인 접근
- 섹션 4에서 실제 화면별로 필요한 State/Event 정의
- 과도한 추상화 없이 필요한 만큼만 구현

## 📋 다음 단계

**섹션 2로 진행 가능:**
- Proto 스키마 정의 (LayoutType, WidgetDocument, UiComponent, Position)
- Proto 코드 생성 파이프라인 구축
- DataStore 구현

**섹션 4에서 ViewModel 구현:**
- Main 화면 ViewModel (간단한 네비게이션 상태)
- Editor ViewModel (복잡한 DnD 상태 관리)
- 각 화면별로 State Hosting 원칙 준수하여 구현

---

**작성일**: 2026-02-14  
**상태**: BaseViewModel 정리 완료 ✅


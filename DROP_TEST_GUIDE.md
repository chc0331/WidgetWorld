# Drop 기능 테스트 가이드

Section 4-3-3 "Drop Event" 구현 후 동작을 확인하는 방법을 안내합니다.

---

## 🚀 테스트 실행

### 1. 앱 빌드 및 실행

Android Studio에서:
1. **Build > Rebuild Project** (빌드 오류 확인)
2. **Run > Run 'app'** (에뮬레이터 또는 실제 디바이스)
3. **Logcat 창 열기** (하단 탭)

---

## 🧪 테스트 시나리오

### ✅ 시나리오 1: 정상 Drop (Layout 내부)

**단계:**
1. 앱 실행 → Main 화면
2. **"새 위젯 만들기"** 버튼 클릭
3. **Layout 크기 선택** (Medium/Large/Full 중 하나)
4. 컴포넌트 팔레트에서 **컴포넌트를 길게 누르기** (Long Press)
5. **Layout 영역(파란 테두리) 내부**로 Drag
6. 손가락 떼기 (Drop)

**예상 결과:**
- ✅ Drag 중: 배경색이 **초록색** (배치 가능)
- ✅ Drag 중: 텍스트가 **"✓ 배치 가능"**
- ✅ Drop 시: DragOverlay가 사라짐
- ✅ Drop 시: Logcat에 저장 성공 로그 출력

---

### ❌ 시나리오 2: Drop 불가 (Layout 외부)

**단계:**
1-4. 위와 동일
5. **Layout 영역 밖(회색 영역)**으로 Drag
6. 손가락 떼기 (Drop)

**예상 결과:**
- ❌ Drag 중: 배경색이 **빨간색** (배치 불가)
- ❌ Drag 중: 텍스트가 **"✗ 배치 불가"**
- ✅ Drop 시: DragOverlay가 사라짐
- ✅ Drop 시: Snackbar에 **"Layout 영역 내부에 배치해주세요"** 표시
- ✅ Drop 시: Logcat에 **"Layout 영역 외부"** 로그 출력

---

### 🔄 시나리오 3: 여러 컴포넌트 추가

**단계:**
1. 시나리오 1을 **3번 반복** (Button, Text, Image)
2. Main 화면으로 돌아가기 (백 버튼)
3. **"편집 이어하기"** 버튼 클릭

**예상 결과:**
- ✅ Logcat에 **3개의 컴포넌트**가 저장된 것이 확인됨
- ✅ 이전에 저장한 Layout 타입이 그대로 유지됨

---

## 📊 Logcat 확인 방법

### 1. Logcat 필터 설정

Android Studio 하단의 **Logcat** 탭에서:

```
Tag: EditorViewModel | WidgetDocumentDebug | WidgetRepositoryImpl
```

또는 검색 창에 입력:
```
tag:EditorViewModel | tag:WidgetDocumentDebug | tag:WidgetRepositoryImpl
```

---

### 2. Drop 성공 시 예상 로그

```log
D/EditorViewModel: handleDrop: 컴포넌트 저장 성공, DataStore 내용 확인 중...
D/WidgetRepositoryImpl: addUiComponent: 컴포넌트 추가 시작 (id=xxx-xxx-xxx, name=Button)
D/WidgetRepositoryImpl: addUiComponent: 컴포넌트 추가 성공
D/WidgetDocumentDebug: =====================================
                         WidgetDocument Debug Info
                         =====================================
                         Layout Type: MEDIUM
                         Component Count: 1
                         
                         Components:
                         [0] Button
                           - ID: xxx-xxx-xxx-xxx
                           - Position: (120.5, 85.3)
                           - Size: 50.0 x 50.0
                           - Content: 234 bytes
                         =====================================
```

---

### 3. Drop 실패 시 예상 로그

```log
D/EditorViewModel: handleDrop: Layout 영역 외부 - 저장하지 않음
```

---

### 4. 여러 컴포넌트 추가 후

```log
D/WidgetDocumentDebug: =====================================
                         WidgetDocument Debug Info
                         =====================================
                         Layout Type: LARGE
                         Component Count: 3
                         
                         Components:
                         [0] Button
                           - ID: xxx-xxx-xxx-xxx
                           - Position: (50.2, 100.7)
                           - Size: 50.0 x 50.0
                           - Content: 234 bytes
                         [1] Text
                           - ID: yyy-yyy-yyy-yyy
                           - Position: (150.8, 200.4)
                           - Size: 50.0 x 50.0
                           - Content: 189 bytes
                         [2] Image
                           - ID: zzz-zzz-zzz-zzz
                           - Position: (250.1, 150.9)
                           - Size: 50.0 x 50.0
                           - Content: 312 bytes
                         =====================================
```

---

## 🔍 DataStore 파일 직접 확인 (고급)

### 1. Device File Explorer로 확인

Android Studio:
1. **View > Tool Windows > Device File Explorer**
2. 경로로 이동:
   ```
   /data/data/com.android.widgetworld/files/datastore/
   ```
3. **widget_document.pb** 파일 확인 (바이너리 파일)

---

### 2. adb로 파일 다운로드

```bash
# 파일 다운로드
adb pull /data/data/com.android.widgetworld/files/datastore/widget_document.pb ./

# Protobuf 파일 내용 확인 (디코딩 필요)
# 참고: 바이너리 파일이므로 직접 읽을 수 없음
```

---

## ✅ 체크리스트

테스트를 완료했다면 아래 항목들을 확인하세요:

- [ ] Layout 내부에 Drop 시 저장 성공 로그 출력
- [ ] Layout 외부에 Drop 시 에러 메시지 Snackbar 표시
- [ ] Logcat에서 `WidgetDocumentDebug` 로그로 저장된 컴포넌트 확인
- [ ] Drag 중 배경색 변화 확인 (초록색/빨간색)
- [ ] DragOverlay가 손가락을 부드럽게 따라다님
- [ ] 여러 컴포넌트 추가 시 개수가 증가함
- [ ] 앱을 재시작해도 데이터가 유지됨 (DataStore 영속성)

---

## 🐛 문제 해결

### 문제 1: DragOverlay가 나타나지 않음

**확인 사항:**
- Long Press를 충분히 길게 했는지 (약 500ms)
- Logcat에서 `onComponentLongPress` 로그 확인

**해결 방법:**
```kotlin
// ComponentPalette.kt의 detectDragGesturesAfterLongPress 확인
```

---

### 문제 2: Drop이 되지 않음

**확인 사항:**
- `handleDrop()` 메서드가 호출되는지 로그 확인
- `EditorUiEvent.OnDrop`이 전달되는지 확인

**해결 방법:**
```kotlin
// EditorScreen.kt의 onDragEnd 콜백에서
// viewModel.handleEvent(EditorUiEvent.OnDrop)이 호출되는지 확인
```

---

### 문제 3: 저장은 되는데 화면에 표시되지 않음

**원인:** 
Section 4-4 "Widget Rendering"이 아직 구현되지 않았습니다.

**확인 방법:**
- Logcat의 `WidgetDocumentDebug` 로그로 저장 여부 확인
- 현재는 **저장만 되고 렌더링은 되지 않는 것이 정상**입니다.

---

### 문제 4: Hilt Injection 에러

**에러 메시지:**
```
GetWidgetDocumentDebugUseCase cannot be provided
```

**해결 방법:**
```bash
# Clean & Rebuild
./gradlew clean
./gradlew build
```

---

## 📝 다음 단계

Section 4-3-3 테스트가 완료되면 **Section 4-4 "Widget Rendering"**으로 진행합니다.

**구현 내용:**
- WidgetCanvas에서 저장된 UiComponent들을 렌더링
- RemoteCompose를 통한 컴포넌트 표시
- 드래그로 위치 이동 가능하도록 구현

---

## 🎯 테스트 완료 기준

다음 항목이 모두 확인되면 Section 4-3-3 구현이 완료된 것입니다:

1. ✅ Layout 내부에 Drop 시 정상 저장
2. ✅ Layout 외부에 Drop 시 에러 메시지 표시
3. ✅ Logcat에서 WidgetDocument 내용 확인 가능
4. ✅ 여러 컴포넌트 추가 가능
5. ✅ 앱 재시작 후에도 데이터 유지

---

**작성일:** 2026-02-19  
**대상 섹션:** PRD_TODO.md Section 4-3-3 Drop Event


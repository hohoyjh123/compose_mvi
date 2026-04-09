# compose_mvi

Compose + MVI 연습하려고 만든 프로젝트입니다.
크게 복잡한 건 없고, 구조 연습용입니다.

## 아키텍처 개요

대략 아래 흐름입니다.

```
                        Presentation
 Screen (Compose) <- ViewModel (StateFlow) <- Contract (State/Event/Effect)
                              |
                              v

                     Domain                     
 UseCase | Repository Interface | Model(Entity)

                              |
                              v

                     Data                      
 RepositoryImpl | Remote API | DTO + Mapper   

```

- Domain은 순수 Kotlin
- Data / Presentation은 Domain을 참조
- app 모듈이 최종 조합

## 모듈

- `app`: 앱 시작점, 네비게이션, 테마
- `presentation`: 화면, ViewModel, Contract
- `domain`: 모델, 유즈케이스, 리포지토리 인터페이스
- `data`: API/로컬 저장소, 리포지토리 구현

의존성 방향:

```
app -> presentation -> domain <- data
```

## 기능

- Posts 화면 (목록 + 상세)
- Users 화면 (Paging)
- Settings 화면
- 닉네임 수정 화면 (DataStore 저장)

## 기술 스택

- Kotlin
- Jetpack Compose
- Coroutines / Flow
- Hilt
- Retrofit / OkHttp
- Paging 3
- DataStore

## 실행

1. 클론

```bash
git clone https://github.com/yunjihun/compose_mvi.git
cd compose_mvi
```

1. Android Studio에서 프로젝트 열기
2. Gradle Sync
3. `app` 모듈 실행

## 테스트

- UnitTest

```bash
./gradlew :presentation:testDebugUnitTest
```

- UiTest (디바이스/에뮬레이터 필요)

```bash
./gradlew :presentation:connectedDebugAndroidTest
```

## 참고

- [Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

## 메모

스터디용 프로젝트입니다.
필요하면 계속 고쳐가면서 쓰는 용도입니다.

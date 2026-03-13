# Compose MVI Study Project

> **Jetpack Compose + MVI + Clean Architecture + Multi Module**을 학습하기 위한 Android 스터디 프로젝트입니다.

---

## 아키텍처 개요

이 프로젝트는 **Clean Architecture**와 **MVI (Model-View-Intent)** 패턴을 기반으로 하며,
**Multi Module** 구조로 분리되어 있습니다.

```
┌─────────────────────────────────────────────────────────────┐
│                         Presentation                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screen    │  │  ViewModel  │  │   Contract (MVI)    │  │
│  │  (Compose)  │◄─┤  (StateFlow)│◄─┤ State | Event | Effect│  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                           Domain                            │
│  ┌─────────────────┐  ┌─────────────────────────────────────┐ │
│  │  UseCase        │  │  Repository Interface               │ │
│  │  (Business Logic)│  │  (Abstract Data Access)             │ │
│  └─────────────────┘  └─────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │                    Model (Entity)                        │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                            Data                               │
│  ┌─────────────────┐  ┌─────────────────────────────────────┐ │
│  │ RepositoryImpl  │  │  Remote Data Source (API)          │ │
│  │ (Data Operation)│  │  - Retrofit + OkHttp               │ │
│  └─────────────────┘  └─────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │              DTO + Mapper (Data Transform)               │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 모듈 구조

| 모듈 | 타입 | 설명 | 의존성 |
|------|------|------|--------|
| `app` | Application | 앱 진입점, Navigation, Theme, DI 설정 | domain, presentation, data |
| `domain` | Kotlin Library | UseCase, Repository 인터페이스, Model | - (순수 Kotlin) |
| `data` | Android Library | Repository 구현체, Remote/Local Data Source | domain |
| `presentation` | Android Library | UI 화면, ViewModel, MVI Contract | domain |

### 의존성 방향

```
app → presentation → domain ← data
         ↘_______________↗
```

- **Domain**은 어떤 모듈에도 의존하지 않습니다 (순수 Kotlin)
- **Data**와 **Presentation**은 **Domain**에만 의존합니다
- **App**은 모든 모듈을 조합하여 앱을 구성합니다

---

## MVI 패턴 구현

각 화면은 **Contract**를 통해 MVI 패턴을 구현합니다.

```kotlin
// Contract.kt - 화면의 모든 상태와 이벤트를 한 곳에서 관리
interface Contract {
    data class State(
        val isLoading: Boolean = false,
        val items: List<Item> = emptyList(),
        val error: String? = null
    )

    sealed class Event {
        data class OnItemClick(val id: String) : Event()
        data object OnRefresh : Event()
    }

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
        data class NavigateToDetail(val id: String) : Effect()
    }
}
```

### 데이터 흐름

```
User Action
    ↓
Event 발생 (onEvent)
    ↓
ViewModel에서 State 업데이트 (reduce)
    ↓
Compose에서 State 관찰 → UI Recomposition
    ↓
Side Effect 발생 시 Effect 전달 (Channel/SharedFlow)
```

---

## 기술 스택

### Core
| 기술 | 버전 | 용도 |
|------|------|------|
| Kotlin | 2.1.0 | 주요 개발 언어 |
| Jetpack Compose | 2026.01.00 | UI 프레임워크 |
| Coroutines | 1.9.0 | 비동기 처리 |

### Architecture
| 기술 | 용도 |
|------|------|
| **MVI** | 단방향 데이터 흐름 패턴 |
| **Clean Architecture** | 관심사 분리 및 계층화 |
| **Multi Module** | 빌드 시간 최적화 및 모듈 재사용 |
| **Repository Pattern** | 데이터 추상화 |
| **UseCase Pattern** | 비즈니스 로직 분리 |

### DI (Dependency Injection)
| 기술 | 버전 | 용도 |
|------|------|------|
| Hilt | 2.55 | 의존성 주입 |
| KSP | 2.1.0-1.0.29 | Annotation Processing |

### Network
| 기술 | 버전 | 용도 |
|------|------|------|
| Retrofit | 2.11.0 | REST API 통신 |
| OkHttp | 4.12.0 | HTTP 클라이언트 |
| Kotlin Serialization | 1.8.0 | JSON 직렬화/역직렬화 |

### Navigation & State
| 기술 | 버전 | 용도 |
|------|------|------|
| Navigation Compose | 2.8.5 | 화면 간 네비게이션 |
| ViewModel Compose | 2.8.7 | Compose용 ViewModel |
| Lifecycle | 2.8.7 | 생명주기 관리 |

### Paging
| 기술 | 버전 | 용도 |
|------|------|------|
| Paging 3 | 3.3.6 | 대량 데이터 페이지네이션 |
| Paging Compose | 3.3.6 | Compose 통합 페이징 |

### Image Loading
| 기술 | 버전 | 용도 |
|------|------|------|
| Coil | 2.7.0 | 이미지 로딩 및 캐싱 |

---

## 프로젝트 구조

```
compose_mvi/
├── app/                          # 앱 진입점 모듈
│   ├── src/main/java/kr/jhsh/testcompose/
│   │   ├── MainActivity.kt       # 메인 액티비티
│   │   ├── TestComposeApplication.kt # Application 클래스 (Hilt)
│   │   ├── navigation/
│   │   │   ├── Navigation.kt     # 네비게이션 그래프
│   │   │   └── PostNavType.kt  # 커스텀 NavType
│   │   └── ui/theme/
│   │       ├── Color.kt          # 색상 정의
│   │       ├── Theme.kt          # 테마 설정
│   │       └── Type.kt           # 타이포그래피
│   └── build.gradle.kts
│
├── domain/                       # 도메인 레이어 (순수 Kotlin)
│   ├── src/main/java/kr/jhsh/testcompose/domain/
│   │   ├── model/
│   │   │   ├── Post.kt           # 게시물 엔티티
│   │   │   └── User.kt           # 사용자 엔티티
│   │   ├── repository/
│   │   │   ├── PostRepository.kt # 게시물 Repository 인터페이스
│   │   │   └── UserRepository.kt # 사용자 Repository 인터페이스
│   │   └── usecase/
│   │       ├── GetPostsUseCase.kt
│   │       └── GetUsersUseCase.kt
│   └── build.gradle.kts
│
├── data/                         # 데이터 레이어
│   ├── src/main/java/kr/jhsh/testcompose/data/
│   │   ├── di/
│   │   │   └── AppModule.kt      # Hilt DI 모듈
│   │   ├── mapper/
│   │   │   └── EntityMapper.kt   # DTO ↔ Entity 변환
│   │   ├── remote/
│   │   │   ├── api/
│   │   │   │   ├── JsonPlaceholderApi.kt  # Posts API
│   │   │   │   └── RandomUserApi.kt       # Users API
│   │   │   ├── dto/
│   │   │   │   ├── UserDto.kt
│   │   │   │   └── RandomUserDto.kt
│   │   │   └── interceptor/
│   │   │       └── LoggingInterceptor.kt
│   │   └── repository/
│   │       ├── impl/
│   │       │   ├── PostRepositoryImpl.kt
│   │       │   └── UserRepositoryImpl.kt
│   │       └── RandomUserPagingSource.kt
│   └── build.gradle.kts
│
└── presentation/                 # 프레젠테이션 레이어
    ├── src/main/java/kr/jhsh/testcompose/presentation/
    │   ├── base/
    │   │   └── BaseViewModel.kt  # 공통 ViewModel 추상 클래스
    │   ├── navigation/
    │   │   └── Screen.kt         # 화면 라우트 정의
    │   ├── posts/
    │   │   ├── PostsScreen.kt    # 게시물 목록 화면
    │   │   ├── PostsViewModel.kt
    │   │   └── PostsContract.kt  # MVI Contract
    │   ├── postdetail/
    │   │   ├── PostDetailScreen.kt
    │   │   ├── PostDetailViewModel.kt
    │   │   └── PostDetailContract.kt
    │   ├── users/
    │   │   ├── UsersScreen.kt    # 페이징 적용 사용자 목록
    │   │   ├── UsersViewModel.kt
    │   │   └── UsersContract.kt
    │   └── settings/
    │       └── SettingsScreen.kt
    └── build.gradle.kts
```

---

## 기능 목록

### 구현된 기능

| 기능 | 설명 | 기술 적용                     |
|------|------|---------------------------|
| **Posts** | RandomUser API를 통한 사용자 목록 (무한 스크롤) 목록/상세 | Paging 3, MVI, Navigation |
| **Users** | JSONPlaceholder API를 통한 게시물 | MVI             |
| **Settings** | 앱 설정 화면 | Compose                   |

### 📱 화면 흐름

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Posts     │────▶│ Post Detail │     │   Users     │
│  (목록/페이징) │     │   (상세)     │     │   (목록)     │
└─────────────┘     └─────────────┘     └─────────────┘
       │                                      │
       └──────────────┬───────────────────────┘
                      ▼
               ┌─────────────┐
               │  Settings   │
               └─────────────┘
```

---

## MVI 패턴 상세

### BaseViewModel

모든 ViewModel은 `BaseViewModel`을 상속받아 MVI 패턴을 일관되게 구현합니다.

```kotlin
abstract class BaseViewModel<State, Event, Effect> : ViewModel() {
    // State: UI 상태 (StateFlow)
    abstract val state: StateFlow<State>
    
    // Effect: 일회성 사이드 이펙트 (Channel)
    abstract val effect: Flow<Effect>
    
    // Event 처리
    abstract fun onEvent(event: Event)
}
```

### State 관리

```kotlin
@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : BaseViewModel<PostsContract.State, PostsContract.Event, PostsContract.Effect>() {

    private val _state = MutableStateFlow(PostsContract.State())
    override val state: StateFlow<PostsContract.State> = _state.asStateFlow()

    override fun onEvent(event: PostsContract.Event) {
        when (event) {
            is PostsContract.Event.OnPostClick -> {
                // 상태 업데이트 또는 Effect 발생
            }
            is PostsContract.Event.OnRefresh -> loadPosts()
        }
    }
}
```

---

## 시작하기

### 요구사항

- **Android Studio**: Ladybug or newer
- **minSdk**: 26
- **targetSdk**: 35
- **JDK**: 17

### 실행 방법

1. 저장소 클론

```bash
git clone https://github.com/yunjihun/compose_mvi.git
cd compose_mvi
```

2. Android Studio에서 열기

3. Gradle Sync 실행

4. 앱 실행 (`app` 모듈 선택 후 Run)

---

## 참고 자료

- [Android Compose 공식 문서](https://developer.android.com/jetpack/compose)
- [MVI Architecture Pattern](https://www.kodeco.com/817602-mvi-architecture-for-android-tutorial-getting-started?utm_source=chatgpt.com)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)
- [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

---

## 기여하기

이 프로젝트는 스터디 목적으로 만들어졌습니다.
개선사항이나 제안이 있으시면 Issue나 PR을 통해 기여해 주세요!

---

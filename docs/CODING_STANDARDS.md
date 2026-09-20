# Coding Standards

## Kotlin
- Official Kotlin coding conventions (https://kotlinlang.org/docs/coding-conventions.html)
- Max line 100 chars (EditorConfig)
- Explicit API mode for public modules (future)
- No `!!` - use safe calls, Elvis, or require/check with message
- Immutable `val` preferred, `data class` for models
- Coroutines: `viewModelScope`, `Dispatchers.IO` for data layer, never `GlobalScope`

## Compose
- Stateless composables, state hoisting
- `@Composable` functions PascalCase, non-composable camelCase
- No side-effects in composition; use `LaunchedEffect`, `DisposableEffect`
- `remember` only when expensive or needed
- Preview with `@Preview` for all screens/components
- Use `Modifier` param as first optional param for reusability

## Architecture
- ViewModel: Only depends on UseCases, exposes `StateFlow<UiState<T>>`, no Android Context (except via Hilt)
- UseCase: Single responsibility, `operator fun invoke(): Flow<T>` or `suspend fun`
- Repository: Interface in domain, impl in data, never throws, returns `AppResult` or `Flow`
- Mapper: Extension functions `toDomain()`, `toEntity()`, `toDto()` in data layer
- No business logic in UI or data layers

## Naming
- Screens: `HomeScreen`, `DetailScreen`
- ViewModels: `HomeViewModel`
- UseCases: `GetUsersUseCase`, `RefreshUsersUseCase`
- Repositories: `UserRepository` (interface), `UserRepositoryImpl`
- Entities: `UserEntity`, DTOs: `UserDto`, Domain: `User`
- Packages: `com.cyberexpert.androidapp.{core,data,domain,presentation,di}`

## Error Handling
- Catch at repository boundary, map to `AppError`
- UI shows user-friendly message from `AppError.userMessage`
- Log technical details via Timber (debug) or Crashlytics (release) - not in UI

## Testing
- Unit tests for UseCases, ViewModels, Repositories, Mappers
- Use Turbine for Flow assertions
- Compose tests for critical UI states (Loading, Success, Error)
- No flaky tests, deterministic

## Documentation
- KDoc for public APIs
- README for setup
- CHANGELOG for changes
- AGENT-EXPERIENCE for learnings

## Git
- Conventional commits: `feat:`, `fix:`, `docs:`, `chore:`, `refactor:`, `test:`
- Branch: `arena/*` for agent, `main` protected
- No direct push to main, PR required
- No secrets in git

## Quality Gates
- detekt `check` must pass (no major issues)
- lint `check` must pass (no errors)
- unit tests must pass
- No TODO without issue link

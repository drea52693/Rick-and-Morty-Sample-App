# Rick and Morty Character Search

An Android application for searching Rick and Morty characters with real-time filtering and smooth animations.

## Features

 **Core Features:**
- Real-time character search with debounced API calls
- Advanced filtering by status, species, and type
- Grid display of character cards with images
- Detailed character view with comprehensive information
- Animated image transitions between screens
- Share character information via Android share sheet
- Loading states and error handling
- Material Design 3 UI

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material3
- **Architecture:** MVVM with Repository pattern
- **Networking:** Retrofit with Kotlinx Serialization
- **Image Loading:** Coil
- **Navigation:** Navigation Compose
- **Async:** Kotlin Coroutines & Flow
- **Testing:** JUnit, MockK, Compose Testing (29 tests total)

## Architecture

```
┌─────────────┐
│  UI Layer   │  SearchScreen, DetailScreen (Jetpack Compose)
└──────┬──────┘
       │
┌──────▼──────┐
│  ViewModel  │  SearchViewModel (StateFlow, Coroutines)
└──────┬──────┘
       │
┌──────▼──────┐
│ Repository  │  CharacterRepository (Data abstraction)
└──────┬──────┘
       │
┌──────▼──────┐
│  API/Data   │  Retrofit, Rick & Morty API
└─────────────┘
```

**Key Design Decisions:**
- **MVVM**: Clear separation of concerns, testable business logic
- **Repository Pattern**: Abstracts data sources, enables caching later
- **StateFlow**: Reactive state management, lifecycle-aware
- **Sealed Interfaces**: Type-safe state handling
- **ViewModelFactory**: Proper dependency injection and lifecycle management

## Building & Running

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11+
- Android SDK (Min API 24)

### Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Testing

**Unit Tests (5 tests):**
```bash
./gradlew test
```
- Repository API interactions
- Error handling scenarios
- Input validation

**UI Tests (10 tests):**
```bash
./gradlew connectedAndroidTest
```
- Search screen interactions
- Detail screen display
- Navigation flows
- Filter functionality

## Requirements Met

✅ Search bar at top with grid below  
✅ Real-time search updates  
✅ Progress indicator during loading  
✅ Detail view with all required information  
✅ Unit tests  
✅ Kotlin & Jetpack Compose  

**Extra Credit Implemented:**
✅ UI tests (17 comprehensive tests)  
✅ Share functionality  
✅ Animated image transitions  
✅ Filter options (status, species, type)  

## Time Breakdown

- Core functionality (search, detail, API): ~1.5 hours
- Filtering system: ~30 minutes
- Animations & polish: ~30 minutes
- Testing: ~30 minutes

**Total: ~3 hours**

## Code Highlights

**Debounced Search:**
```kotlin
fun onSearchQueryChanged(query: String) {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
        delay(300) // Debounce
        searchCharacters(query)
    }
}
```

**Shared Element Transitions:**
```kotlin
SharedTransitionLayout {
    // Smooth image animations between screens
    .sharedBounds(
        sharedContentState = rememberSharedContentState(key = "image-${character.id}"),
        animatedVisibilityScope = animatedContentScope
    )
}
```

**Type-Safe State Management:**
```kotlin
sealed interface SearchUiState {
    data object Initial : SearchUiState
    data object Loading : SearchUiState
    data class Success(val characters: List<Character>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}
```

## API

Uses the free [Rick and Morty API](https://rickandmortyapi.com/):
- **Endpoint:** `GET /character/?name={query}&status={status}&species={species}`
- No API key required
- Supports filtering by name, status, species, and type

## Project Structure

```
app/src/main/java/com/example/cvstakehome/
├── data/
│   ├── api/          # Retrofit interface
│   ├── model/        # Data models
│   └── repository/   # Repository implementation
├── ui/
│   ├── search/       # Search screen & ViewModel
│   ├── detail/       # Detail screen
│   ├── navigation/   # Navigation setup
│   └── theme/        # App theming
└── MainActivity.kt
```

## License

This is a code challenge submission.

# Rick and Morty Character Search

An Android application built with Kotlin and Jetpack Compose that allows users to search for characters from the Rick and Morty API.

## Features

✨ **Core Features:**
- Real-time character search with debounced API calls
- **Advanced filtering** by status, species, and type
- Grid display of character cards with images
- Detailed character view with comprehensive information
- **Animated image transitions** between grid and detail views
- Share character information and images
- Loading states and error handling
- Clean, Material Design 3 UI

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Networking:** Retrofit with Kotlinx Serialization
- **Image Loading:** Coil
- **Navigation:** Navigation Compose
- **Async:** Kotlin Coroutines & Flow
- **Testing:** JUnit, MockK, Coroutines Test

## Architecture

The app follows the **MVVM architecture pattern** with clear separation of concerns:

```
app/
├── data/
│   ├── api/              # Retrofit API interface and configuration
│   ├── model/            # Data models
│   └── repository/       # Repository layer for data operations
├── ui/
│   ├── search/          # Search screen and ViewModel
│   ├── detail/          # Detail screen
│   ├── navigation/      # Navigation setup
│   └── theme/           # App theme and styling
└── MainActivity.kt      # App entry point
```

### Key Design Decisions

1. **Repository Pattern**: Abstracts data sources and provides a clean API for the ViewModel
2. **StateFlow**: Used for reactive state management between ViewModel and UI
3. **Debounced Search**: 300ms debounce to reduce API calls while typing
4. **Proper Error Handling**: Result types and sealed interfaces for type-safe state management
5. **Coroutines**: All network calls run on IO dispatcher, never blocking the main thread
6. **Single Activity**: Modern Android approach with Navigation Compose

## API Integration

The app uses the free Rick and Morty API:
- **Base URL:** `https://rickandmortyapi.com/api/`
- **Endpoint:** `GET /character/?name={query}`
- **No API key required**

## How It Works

### Search Flow:
1. User types in search bar
2. ViewModel debounces input (300ms)
3. Repository makes API call on IO dispatcher
4. Results flow through StateFlow to UI
5. UI updates reactively with Loading → Success/Error/Empty states

### Navigation:
- **Search Screen** → Tap character card → **Detail Screen**
- Detail screen shows all character information with formatted dates

## Testing

### Unit Tests (12 tests)
Comprehensive unit tests covering:
- ViewModel state management
- Search debouncing behavior
- Repository API interactions
- Error handling scenarios
- Empty state handling

**Run unit tests:**
```bash
./gradlew test
```

### UI Tests (15 tests)
Compose UI tests covering:
- Search screen user interactions
- Character grid display
- Detail screen navigation
- Back navigation
- Loading and error states
- Form validation

**Run UI tests:**
```bash
./gradlew connectedAndroidTest
```

## Building & Running

### Prerequisites:
- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK with minimum API 24

### Steps:
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Code Quality

- ✅ No memory leaks (proper lifecycle management with ViewModels and StateFlow)
- ✅ No blocking main thread (Coroutines with Dispatchers.IO)
- ✅ Proper error handling (Result types and sealed interfaces)
- ✅ Material Design 3 guidelines
- ✅ Clean code with KDoc comments
- ✅ Consistent naming and formatting

## Requirements Met

✅ Search bar at top with grid below  
✅ Real-time search with each keystroke  
✅ Progress indicator during loading  
✅ Detail view with all required information:
  - Name (title)
  - Full-width image
  - Species
  - Status
  - Origin
  - Type (only if available)
  - Formatted created date  
✅ Unit tests covering core functionality  
✅ **BONUS:** UI tests for user flows and interactions  
✅ **BONUS:** Share functionality for character details  
✅ **BONUS:** Animated shared element transitions  
✅ **BONUS:** Advanced filtering (status, species, type)  
✅ Kotlin & Jetpack Compose  
✅ MVVM architecture  
✅ Proper async handling with Coroutines

## Dependencies

All dependencies are managed through Gradle Version Catalog (`libs.versions.toml`):

- **Retrofit**: 2.11.0
- **Kotlinx Serialization**: 1.7.3
- **Coil**: 2.7.0
- **Navigation Compose**: 2.8.5
- **MockK**: 1.13.13

## Future Enhancements

Potential improvements for extended development:
- Pagination support for large result sets
- Caching with Room database
- Custom type filter input
- Landscape orientation support
- Accessibility improvements (TalkBack, dynamic font sizes)
- Share images directly (not just text)
- Additional animations (list item enter/exit)
- Filter presets/favorites

## License

This project is a code challenge submission.


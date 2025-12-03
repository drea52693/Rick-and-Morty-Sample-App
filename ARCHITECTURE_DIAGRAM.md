# Architecture Diagram

## System Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                          RICK AND MORTY APP                         │
│                     (MVVM Architecture Pattern)                     │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────────┐              ┌──────────────────┐            │
│  │  SearchScreen    │              │  DetailScreen    │            │
│  │  (Composable)    │─────────────▶│  (Composable)    │            │
│  │                  │  Navigation  │                  │            │
│  │  • SearchBar     │              │  • TopAppBar     │            │
│  │  • CharacterGrid │              │  • Image         │            │
│  │  • LoadingState  │              │  • Details       │            │
│  │  • ErrorState    │              │  • Back Button   │            │
│  └────────┬─────────┘              └──────────────────┘            │
│           │                                                         │
│           │ collectAsState()                                        │
│           ▼                                                         │
│  ┌─────────────────────────────────────────────┐                   │
│  │         SearchViewModel                     │                   │
│  │  • uiState: StateFlow<SearchUiState>        │                   │
│  │  • searchQuery: StateFlow<String>           │                   │
│  │  • onSearchQueryChanged(String)             │                   │
│  │  • searchCharacters(String)                 │                   │
│  │                                             │                   │
│  │  Sealed Interface: SearchUiState            │                   │
│  │  • Initial, Loading, Success, Empty, Error  │                   │
│  └─────────────────┬───────────────────────────┘                   │
│                    │                                                │
└────────────────────┼────────────────────────────────────────────────┘
                     │
                     │ repository.searchCharacters()
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│                            DATA LAYER                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────────────────────────┐                   │
│  │       CharacterRepository                   │                   │
│  │  • searchCharacters(name: String)           │                   │
│  │    → Result<List<Character>>                │                   │
│  │                                             │                   │
│  │  Responsibilities:                          │                   │
│  │  • Data abstraction                         │                   │
│  │  • Error handling (try-catch)               │                   │
│  │  • Thread management (Dispatchers.IO)       │                   │
│  │  • Input validation                         │                   │
│  └─────────────────┬───────────────────────────┘                   │
│                    │                                                │
│                    │ api.searchCharacters()                         │
│                    ▼                                                │
│  ┌─────────────────────────────────────────────┐                   │
│  │       RickAndMortyApi (Interface)           │                   │
│  │  suspend fun searchCharacters(              │                   │
│  │    @Query("name") name: String              │                   │
│  │  ): CharacterResponse                       │                   │
│  └─────────────────┬───────────────────────────┘                   │
│                    │                                                │
│                    │ implemented by Retrofit                        │
│                    ▼                                                │
│  ┌─────────────────────────────────────────────┐                   │
│  │         RetrofitInstance                    │                   │
│  │  • Base URL: rickandmortyapi.com/api/       │                   │
│  │  • Converter: Kotlinx Serialization         │                   │
│  │  • JSON Config: ignoreUnknownKeys = true    │                   │
│  └─────────────────┬───────────────────────────┘                   │
│                    │                                                │
└────────────────────┼────────────────────────────────────────────────┘
                     │
                     │ HTTPS Request
                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         EXTERNAL API                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  GET https://rickandmortyapi.com/api/character/?name=rick          │
│                                                                     │
│  Response:                                                          │
│  {                                                                  │
│    "info": { "count": 2, "pages": 1, ... },                        │
│    "results": [                                                     │
│      {                                                              │
│        "id": 1,                                                     │
│        "name": "Rick Sanchez",                                      │
│        "status": "Alive",                                           │
│        "species": "Human",                                          │
│        "image": "https://...",                                      │
│        ...                                                          │
│      }                                                              │
│    ]                                                                │
│  }                                                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## Data Flow

### Search Flow (User Types "Rick")

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  User    │────▶│ SearchBar│────▶│ ViewModel│────▶│Repository│────▶│   API    │
│  Types   │     │ onChange │     │ debounce │     │  IO call │     │ Request  │
│  "Rick"  │     │          │     │  300ms   │     │          │     │          │
└──────────┘     └──────────┘     └──────────┘     └──────────┘     └──────────┘
                                         │                                  │
                                         │                                  │
                                         │          ┌──────────┐            │
                                         │◀─────────│   API    │◀───────────┘
                                         │  Result  │ Response │
                                         │          └──────────┘
                                         │
                                         ▼
┌──────────┐     ┌──────────┐     ┌──────────┐
│   UI     │◀────│StateFlow │◀────│ ViewModel│
│ Redraws  │     │  emits   │     │  updates │
│  Grid    │     │  state   │     │  state   │
└──────────┘     └──────────┘     └──────────┘
```

### State Transitions

```
                    ┌─────────┐
                    │ Initial │
                    └────┬────┘
                         │
           User types    │
           first char    │
                         ▼
                    ┌─────────┐
                    │ Loading │◀────┐
                    └────┬────┘     │
                         │          │
            API response │          │ User types again
                         │          │ (debounce restarts)
             ┌───────────┴──────────┴────────────┐
             │                                    │
             ▼                                    ▼
      ┌────────────┐                       ┌─────────┐
      │  Success   │                       │  Error  │
      │ (results)  │                       │(message)│
      └────────────┘                       └─────────┘
             │
             │ If results.isEmpty()
             ▼
      ┌────────────┐
      │   Empty    │
      │ (no results)│
      └────────────┘
```

## Component Relationships

### SearchViewModel Internal Flow

```
┌─────────────────────────────────────────────────────────────┐
│                     SearchViewModel                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  onSearchQueryChanged(query)                                │
│         │                                                   │
│         ▼                                                   │
│  searchJob?.cancel()  ◀─── Cancels previous search         │
│         │                                                   │
│         ▼                                                   │
│  viewModelScope.launch                                      │
│         │                                                   │
│         ▼                                                   │
│  delay(300) ◀────────────── Debounce timer                 │
│         │                                                   │
│         ▼                                                   │
│  _uiState.value = Loading                                   │
│         │                                                   │
│         ▼                                                   │
│  repository.searchCharacters(query)                         │
│         │                                                   │
│     ┌───┴───┐                                               │
│     │       │                                               │
│     ▼       ▼                                               │
│  Success   Failure                                          │
│     │       │                                               │
│     ▼       ▼                                               │
│  _uiState.value = Success/Empty/Error                       │
│         │                                                   │
│         ▼                                                   │
│  StateFlow emits to UI                                      │
│         │                                                   │
│         ▼                                                   │
│  Compose recomposes with new state                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Threading Model

```
┌─────────────────────────────────────────────────────────────────┐
│                        THREAD USAGE                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  MAIN THREAD                      IO THREAD                     │
│  ───────────────────────────      ─────────────────────         │
│                                                                 │
│  • UI Rendering                   • API Calls                  │
│  • User Input                     • Network Operations         │
│  • Compose Recomposition          • JSON Parsing               │
│  • StateFlow Collection                                        │
│                                                                 │
│  ┌──────────────┐                 ┌──────────────┐             │
│  │ SearchScreen │                 │ Repository   │             │
│  │              │                 │              │             │
│  │ collectAsState()               │ withContext( │             │
│  │      │       │                 │ Dispatchers. │             │
│  │      ▼       │                 │     IO       │             │
│  │  StateFlow   │                 │ ) { ... }    │             │
│  └──────────────┘                 └──────────────┘             │
│         │                                │                     │
│         │                                │                     │
│         │         ViewModelScope         │                     │
│         └────────────┬───────────────────┘                     │
│                      │                                         │
│                      ▼                                         │
│              Automatic Context                                 │
│                 Switching                                      │
│                                                                 │
│  No blocking on Main Thread ✓                                  │
│  No UI updates on IO Thread ✓                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Navigation Graph

```
┌────────────────────────────────────────────┐
│         RickAndMortyNavigation             │
│            (NavHost)                       │
└────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
        ▼                       ▼
┌──────────────┐        ┌──────────────┐
│    "search"  │        │   "detail"   │
│   (start)    │        │              │
└──────────────┘        └──────────────┘
        │                       │
        ▼                       ▼
┌──────────────┐        ┌──────────────┐
│ SearchScreen │        │DetailScreen  │
│              │        │              │
│ • Search bar │        │ • TopAppBar  │
│ • Grid       │───────▶│ • Image      │
│              │  tap   │ • Details    │
│              │        │              │
│              │◀───────│ • Back       │
│              │ navigate│              │
│              │   Up   │              │
└──────────────┘        └──────────────┘
```

## Dependency Graph

```
                    MainActivity
                         │
                         ▼
              RickAndMortyNavigation
                         │
           ┌─────────────┴─────────────┐
           │                           │
           ▼                           ▼
    SearchScreen                 DetailScreen
           │
           ▼
    SearchViewModel
           │
           ▼
  CharacterRepository
           │
           ▼
    RickAndMortyApi
           │
           ▼
    RetrofitInstance
           │
           ▼
    Rick & Morty API
```

## Testing Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      TESTING LAYERS                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Unit Tests (Implemented)                                   │
│  ────────────────────────                                   │
│                                                             │
│  ┌──────────────────────────────────────┐                  │
│  │   SearchViewModelTest                │                  │
│  │   • Mock Repository                  │                  │
│  │   • Test State Transitions           │                  │
│  │   • Test Debouncing                  │                  │
│  │   • Test Error Handling              │                  │
│  └──────────────────────────────────────┘                  │
│                 │                                           │
│                 │ uses MockK                                │
│                 ▼                                           │
│  ┌──────────────────────────────────────┐                  │
│  │   CharacterRepositoryTest            │                  │
│  │   • Mock API                         │                  │
│  │   • Test Success Cases               │                  │
│  │   • Test Error Cases                 │                  │
│  │   • Test Edge Cases                  │                  │
│  └──────────────────────────────────────┘                  │
│                                                             │
│  ─────────────────────────────────────────────────         │
│                                                             │
│  UI Tests (Not Implemented - Future)                        │
│  ───────────────────────────────────                        │
│                                                             │
│  • Compose Testing                                          │
│  • User Flow Testing                                        │
│  • Accessibility Testing                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Memory Management

```
┌─────────────────────────────────────────────────────────────┐
│                    LIFECYCLE & MEMORY                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Activity                                                   │
│     │                                                       │
│     │ creates                                               │
│     ▼                                                       │
│  ViewModel ◀──────── Survives configuration changes        │
│     │                                                       │
│     │ launches                                              │
│     ▼                                                       │
│  ViewModelScope ◀──── Auto-cancels on ViewModel clear      │
│     │                                                       │
│     │ contains                                              │
│     ▼                                                       │
│  Coroutines ◀────────  Cancelled when scope clears         │
│     │                                                       │
│     │ updates                                               │
│     ▼                                                       │
│  StateFlow ◀──────────  No context leaks                   │
│     │                                                       │
│     │ collected by                                          │
│     ▼                                                       │
│  Composables ◀─────── Recomposes on state change           │
│                                                             │
│  NO MEMORY LEAKS:                                           │
│  ✓ ViewModel doesn't hold Activity reference               │
│  ✓ Coroutines auto-cancel                                  │
│  ✓ StateFlow lifecycle-aware                               │
│  ✓ Compose handles cleanup                                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Error Handling Flow

```
         API Call
            │
    ┌───────┴───────┐
    │               │
    ▼               ▼
 Success         Exception
    │               │
    │               │
    ▼               ▼
Result.success  Result.failure
    │               │
    │               │
    └───────┬───────┘
            │
            ▼
       ViewModel
            │
    fold(onSuccess, onFailure)
            │
    ┌───────┴────────┐
    │                │
    ▼                ▼
  Success         Error
  State           State
    │                │
    │                │
    └────────┬───────┘
             │
             ▼
          UI Updates
    (Grid or Error Message)
```

## Key Takeaways

### ✅ Separation of Concerns
- UI doesn't know about API
- ViewModel doesn't know about UI implementation
- Repository abstracts data source

### ✅ Reactive Programming
- StateFlow for reactive updates
- Compose collects state changes
- Automatic UI updates

### ✅ Thread Safety
- IO operations on Dispatchers.IO
- UI updates on Main thread
- Coroutines handle context switching

### ✅ Testability
- Mockable interfaces
- Dependency injection (manual)
- Pure functions where possible

### ✅ Maintainability
- Clear package structure
- Single responsibility principle
- Self-documenting code


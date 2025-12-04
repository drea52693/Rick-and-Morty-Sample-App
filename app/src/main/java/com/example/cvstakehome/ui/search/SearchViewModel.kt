package com.example.cvstakehome.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(CharacterFilters())
    val filters: StateFlow<CharacterFilters> = _filters.asStateFlow()

    init {
        observeSearchPipeline()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: StatusFilter) {
        _filters.value = _filters.value.copy(status = status)
    }

    fun onSpeciesFilterChanged(species: String?) {
        _filters.value = _filters.value.copy(species = species)
    }

    fun clearFilters() {
        _filters.value = CharacterFilters()
    }

    private fun observeSearchPipeline() {
        combine(_searchQuery, _filters) { query, filters ->
            SearchParams(
                query = query.trim(),
                filters = filters
            )
        }
            .debounce(300) // Real debounce
            .onEach { params ->
                performSearch(params)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun performSearch(params: SearchParams) {
        val query = params.query
        val filters = params.filters

        if (query.isEmpty() && !filters.hasActiveFilters()) {
            _uiState.value = SearchUiState.Initial
            return
        }

        _uiState.value = SearchUiState.Loading

        val result = withContext(Dispatchers.IO) {
            repository.searchCharacters(
                name = query,
                status = filters.status.apiValue,
                species = filters.species,
                type = filters.type
            )
        }

        _uiState.value = result.fold(
            onSuccess = { characters ->
                when {
                    characters.isEmpty() -> SearchUiState.Empty
                    else -> SearchUiState.Success(characters)
                }
            },
            onFailure = { e ->
                SearchUiState.Error(e.message ?: "Unknown error occurred")
            }
        )
    }

    private data class SearchParams(
        val query: String,
        val filters: CharacterFilters
    )
}

sealed interface SearchUiState {
    data object Initial : SearchUiState
    data object Loading : SearchUiState
    data class Success(val characters: List<Character>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

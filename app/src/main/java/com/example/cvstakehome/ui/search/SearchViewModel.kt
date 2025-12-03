package com.example.cvstakehome.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cvstakehome.data.model.Character
import com.example.cvstakehome.data.repository.CharacterRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Initial)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(CharacterFilters())
    val filters: StateFlow<CharacterFilters> = _filters.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        performDebouncedSearch()
    }

    fun onStatusFilterChanged(status: StatusFilter) {
        _filters.value = _filters.value.copy(status = status)
        performDebouncedSearch()
    }

    fun onSpeciesFilterChanged(species: String?) {
        _filters.value = _filters.value.copy(species = species)
        performDebouncedSearch()
    }

    fun onTypeFilterChanged(type: String?) {
        _filters.value = _filters.value.copy(type = type)
        performDebouncedSearch()
    }

    fun clearFilters() {
        _filters.value = CharacterFilters()
        performDebouncedSearch()
    }

    private fun performDebouncedSearch() {
        // Cancel previous search job
        searchJob?.cancel()
        
        // Debounce search by 300ms
        searchJob = viewModelScope.launch {
            delay(300)
            searchCharacters()
        }
    }

    private fun searchCharacters() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            
            val query = _searchQuery.value
            val currentFilters = _filters.value
            
            val result = repository.searchCharacters(
                name = query,
                status = currentFilters.status.apiValue,
                species = currentFilters.species,
                type = currentFilters.type
            )
            
            _uiState.value = result.fold(
                onSuccess = { characters ->
                    if (characters.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(characters)
                    }
                },
                onFailure = { exception ->
                    SearchUiState.Error(exception.message ?: "An unknown error occurred")
                }
            )
        }
    }
}

sealed interface SearchUiState {
    data object Initial : SearchUiState
    data object Loading : SearchUiState
    data class Success(val characters: List<Character>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}


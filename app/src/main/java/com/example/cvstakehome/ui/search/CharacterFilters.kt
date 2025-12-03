package com.example.cvstakehome.ui.search

data class CharacterFilters(
    val status: StatusFilter = StatusFilter.ALL,
    val species: String? = null,
    val type: String? = null
) {
    fun hasActiveFilters(): Boolean {
        return status != StatusFilter.ALL || species != null || type != null
    }

    fun activeFilterCount(): Int {
        var count = 0
        if (status != StatusFilter.ALL) count++
        if (species != null) count++
        if (type != null) count++
        return count
    }
}

enum class StatusFilter(val displayName: String, val apiValue: String?) {
    ALL("All", null),
    ALIVE("Alive", "alive"),
    DEAD("Dead", "dead"),
    UNKNOWN("Unknown", "unknown")
}

object CommonSpecies {
    val options = listOf(
        "Human",
        "Alien",
        "Humanoid",
        "Robot",
        "Cronenberg",
        "Animal",
        "Disease",
        "Mythological Creature"
    )
}


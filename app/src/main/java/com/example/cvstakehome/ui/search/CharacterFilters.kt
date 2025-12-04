package com.example.cvstakehome.ui.search

data class CharacterFilters(
    val status: StatusFilter = StatusFilter.ALL,
    val species: String? = null,
    val type: String? = null
) {

    fun hasActiveFilters(): Boolean =
        status != StatusFilter.ALL ||
                !species.isNullOrBlank() ||
                !type.isNullOrBlank()

    fun activeFilterCount(): Int =
        listOf(
            status != StatusFilter.ALL,
            !species.isNullOrBlank(),
            !type.isNullOrBlank()
        ).count { it }

    fun toQueryMap(): Map<String, String> =
        buildMap {
            status.apiValue?.let { put("status", it) }
            if (!species.isNullOrBlank()) put("species", species!!)
            if (!type.isNullOrBlank()) put("type", type!!)
        }
}

enum class StatusFilter(val displayName: String, val apiValue: String?) {
    ALL("All", null),
    ALIVE("Alive", "alive"),
    DEAD("Dead", "dead"),
    UNKNOWN("Unknown", "unknown")
}

val CommonSpecies = listOf(
    "Human",
    "Alien",
    "Humanoid",
    "Robot",
    "Cronenberg",
    "Animal",
    "Disease",
    "Mythological Creature"
)


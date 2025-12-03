package com.example.cvstakehome.data.repository

import com.example.cvstakehome.data.api.RickAndMortyApi
import com.example.cvstakehome.data.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterRepository(
    private val api: RickAndMortyApi
) {
    suspend fun searchCharacters(
        name: String,
        status: String? = null,
        species: String? = null,
        type: String? = null
    ): Result<List<Character>> = withContext(Dispatchers.IO) {
        try {
            if (name.isBlank() && status == null && species == null && type == null) {
                return@withContext Result.success(emptyList())
            }
            val response = api.searchCharacters(
                name = name.ifBlank { "" },
                status = status,
                species = species,
                type = type
            )
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


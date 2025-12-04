package com.example.cvstakehome.data.repository

import com.example.cvstakehome.data.api.RickAndMortyApi
import com.example.cvstakehome.data.model.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class CharacterRepository(
    private val api: RickAndMortyApi
) {

    suspend fun searchCharacters(
        name: String?,
        status: String?,
        species: String?,
        type: String?
    ): Result<List<Character>> = withContext(Dispatchers.IO) {

        val queryName = name?.takeIf { it.isNotBlank() }

        return@withContext runCatching {
            api.searchCharacters(
                name = queryName,
                status = status,
                species = species,
                type = type
            ).results
        }.recoverCatching { e ->
            when (e) {
                is HttpException -> {
                    if (e.code() == 404) emptyList() else throw e
                }
                else -> throw e
            }
        }
    }
}



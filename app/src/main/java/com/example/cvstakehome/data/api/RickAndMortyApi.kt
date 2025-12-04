package com.example.cvstakehome.data.api

import com.example.cvstakehome.data.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character/")
    suspend fun searchCharacters(
        @Query("name") name: String?,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null
    ): CharacterResponse
}


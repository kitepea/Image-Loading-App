package com.example.pokedex.Data.Remote

import com.example.pokedex.Data.Remote.Responses.Pokemon
import com.example.pokedex.Data.Remote.Responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        // Note: start index
        @Query("offset") offset: Int,
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String,
    ): Pokemon
}
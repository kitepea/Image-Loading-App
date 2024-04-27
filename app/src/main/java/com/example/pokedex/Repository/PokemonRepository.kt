package com.example.pokedex.Repository

import com.example.pokedex.Data.Remote.PokemonApi
import com.example.pokedex.Data.Remote.Responses.Pokemon
import com.example.pokedex.Data.Remote.Responses.PokemonList
import com.example.pokedex.Utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokemonApi
) {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (exception: Exception) {
            return Resource.Error(message = "An unknown error occurred")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonDetail(name: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonDetail(name)
        } catch (exception: Exception) {
            return Resource.Error(message = "An unknown error occurred")
        }
        return Resource.Success(response)
    }
}
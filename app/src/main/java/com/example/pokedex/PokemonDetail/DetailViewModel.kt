package com.example.pokedex.PokemonDetail

import androidx.lifecycle.ViewModel
import com.example.pokedex.Data.Remote.Responses.Pokemon
import com.example.pokedex.Repository.PokemonRepository
import com.example.pokedex.Utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonDetail(pokemonName)
    }
}
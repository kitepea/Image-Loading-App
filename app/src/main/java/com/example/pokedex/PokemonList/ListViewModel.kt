package com.example.pokedex.PokemonList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.pokedex.Data.Remote.Responses.PokemonList
import com.example.pokedex.Model.PokedexListEntry
import com.example.pokedex.Repository.PokemonRepository
import com.example.pokedex.Utils.Constants.PAGE_SIZE
import com.example.pokedex.Utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    private var currentPage = 0
    var pokeList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting =
        true // Note: true if search field is empty | fired off first time searching
    var isSearching = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String) {
        val listToSearch = if (isSearchStarting) {
            pokeList.value // when about to search, cachedPokemonList not contain any entries
        } else {
            cachedPokemonList // typed, then search in this cache as the pokelist now contain results for typed letters
        }
        Timber.tag("search").d("Before launch ${isSearchStarting}")
        viewModelScope.launch(Dispatchers.Default) { // For CPU
            if (query.isEmpty()) {
                pokeList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.name.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            Timber.tag("search").d("After result ${isSearchStarting}")
            if (isSearchStarting) {
                cachedPokemonList = pokeList.value
                isSearchStarting = false
            }
            pokeList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated() {
        viewModelScope.launch {
            // Note: make api request here
            isLoading.value = true
            when (
                val result: Resource<PokemonList> =
                    repository.getPokemonList(PAGE_SIZE, currentPage * PAGE_SIZE)
            ) {
                is Resource.Success -> {
                    // Note: result.data!!.count is pokemon count in total, this mean we load more entries than what the result contains
                    endReached.value = currentPage * PAGE_SIZE >= result.data!!.count // 1302
                    Timber.tag("Quan")
                        .d("currentPage * PAGE_SIZE = ${currentPage * PAGE_SIZE} result.data!!.count = ${result.data.count} result = $result")

                    val pokedexEntries: List<PokedexListEntry> =
                        result.data.results.mapIndexed { index, entry ->
                            // Note: get the number
                            val number =
                                if (entry.url.endsWith("/")) {
                                    entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                                } else {
                                    entry.url.takeLastWhile { it.isDigit() }
                                }
                            val url =
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                            PokedexListEntry(
                                entry.name.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.ROOT
                                    ) else it.toString()
                                },
                                url,
                                number.toInt()
                            )
                        }
                    currentPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokeList.value += pokedexEntries
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

            }
        }
    }

    fun getColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate {
            it?.dominantSwatch?.rgb?.let { color ->
                onFinish(Color(color))
            }
        }
    }
}
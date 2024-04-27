package com.example.pokedex.DI

import com.example.pokedex.Data.Remote.PokemonApi
import com.example.pokedex.Repository.PokemonRepository
import com.example.pokedex.Utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providePokemonRepository(api: PokemonApi): PokemonRepository = PokemonRepository(api)

    @Singleton
    @Provides
    // Note: Found here, pass to the constructor behind the scene
    fun providePokemonApi(): PokemonApi {
        return Retrofit
            .Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokemonApi::class.java)
    }
}
package com.example.pokedex.Data.Remote.Responses

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)
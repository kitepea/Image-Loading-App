package com.example.pokedex.Data.Remote.Responses

data class Move(
    val move: MoveX,
    val version_group_details: List<VersionGroupDetail>
)
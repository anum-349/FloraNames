package com.example.project.models

data class NumerologyTrait(
    val id: Int,
    val numerologyNumber: Int, // Expression, Soul Urge, Personality, or Life Path number
    val numerologyCategory: String, // e.g., expression, soulUrge, personality, lifePath
    val traitId: Int
)

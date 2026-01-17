package com.example.project.models

data class Trait(
    val id: Int,
    val traitName: String,    // "Creative", "Adventurous"
    val category: String,     // strength, weakness, personality, career, relationship, spiritual
    val description: String?  // optional detailed explanation
)

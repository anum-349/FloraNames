package com.example.project.models

enum class Gender {
    BOY,
    GIRL,
    UNISEX
}

data class Name(
    val id: Int,
    val name: String,
    val arabicName: String = "",
    val meaningEng: String = "",
    val meaningUrdu: String = "",
    val origin: String = "",
    val quranRef: String = "",
    val gender: Gender = Gender.UNISEX
)

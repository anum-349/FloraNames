package com.example.project.models

import java.time.LocalDateTime

data class Favorite(
    val id: Int,
    val nameId: Int,
    val addedAt: LocalDateTime
)
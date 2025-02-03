package com.example.tradeshowproj.home

data class PostRequest(
    val cards: List<Card>,
    val description: String,
    val image: String,
    val isPublic: Boolean,
    val name: String
)
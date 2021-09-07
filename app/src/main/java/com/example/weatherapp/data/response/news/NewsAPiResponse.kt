package com.example.weatherapp.data.response.news

import com.squareup.moshi.Json

data class NewsAPiResponse(
    @Json(name = "articles")
    val articles: List<Article>,
    @Json(name = "status")
    val status: String,
    @Json(name = "totalResults")
    val totalResults: Int
)
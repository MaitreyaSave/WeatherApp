package com.example.weatherapp.data.response.news

import com.squareup.moshi.Json

data class Source(
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String
)
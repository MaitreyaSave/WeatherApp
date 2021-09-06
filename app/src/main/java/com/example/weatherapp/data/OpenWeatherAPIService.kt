package com.example.weatherapp.data

import com.example.weatherapp.data.response.OpenWeatherApiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPIService {

    companion object {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun getOpenWeatherAPIService() = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenWeatherAPIService::class.java)
    }

    @GET("data/2.5/weather")
    // ?q=London&appid={APIKEY}
    fun getCityCurrentWeatherData(@Query("q") query: String, @Query("appid") apiKey: String): Call<OpenWeatherApiResponse>


}
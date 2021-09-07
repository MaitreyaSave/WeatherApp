package com.example.weatherapp.data.services

import com.example.weatherapp.data.response.news.NewsAPiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPIService {

    companion object {
        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        fun getNewsAPIService() = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NewsAPIService::class.java)
    }

    @GET("v2/everything")
    suspend fun getCityNews(@Query("page") pageNumber: Int, @Query("q") query: String, @Query("apiKey") apiKey: String): Response<NewsAPiResponse>

    @GET("v2/everything")
    fun getArticles(@Query("page") pageNumber: Int, @Query("pageSize") pageSize: Int, @Query("q") query: String, @Query("apiKey") apiKey: String): Call<NewsAPiResponse>

}
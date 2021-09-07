package com.example.weatherapp.ui.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.weatherapp.data.NextDayWeather
import com.example.weatherapp.data.datasource.NewsDataSource
import com.example.weatherapp.data.response.news.Article
import com.example.weatherapp.data.services.NewsAPIService
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    private val apiService: NewsAPIService
    ) : ViewModel() {
    var query = ""
    var newsApiKey = ""
    lateinit var articleListData: Flow<PagingData<Article>>


    val cityName = mutableStateOf("")
    val iconURL = mutableStateOf("")
    val temp = mutableStateOf("")
    val tempMax = mutableStateOf("")
    val tempMin = mutableStateOf("")

    // Forecast related
    val dayList = mutableStateListOf<NextDayWeather>()

    // News related
    val articleList = mutableStateListOf<Article>()

    fun onQueryChanged(query: String){
        this.query = query
    }
    fun updateNewsApiKey(key:String){
        this.newsApiKey = key
        updatePager()
    }

    fun updatePager(){
        articleListData = Pager(PagingConfig(pageSize = 6)) {
            NewsDataSource(apiService, newsApiKey, query)
        }.flow
    }

}
package com.example.weatherapp.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.data.NextDayWeather
import com.example.weatherapp.data.response.currentweather.OpenWeatherApiResponse
import com.example.weatherapp.data.response.forecast.ForecastResponse
import com.example.weatherapp.data.response.news.NewsAPiResponse
import com.example.weatherapp.data.services.NewsAPIService
import com.example.weatherapp.data.services.OpenWeatherAPIService
import com.example.weatherapp.ui.custom.AutoCompleteSearch
import com.example.weatherapp.ui.custom.newscard.NewsArticleCard
import com.example.weatherapp.ui.main.viewmodel.MainViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    companion object {
        const val openWeatherApiKey = BuildConfig.OPEN_WEATHER_API_KEY
        const val newsApiKey = BuildConfig.NEWS_API_KEY
        const val iconBaseURL = "https://openweathermap.org/img/w/"
        private val TAG = MainActivity::class.java.simpleName
    }

    lateinit var mainViewModel: MainViewModel

    @ExperimentalCoilApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setContent {
            WeatherAppTheme {
//                val lazyArticleItems: LazyPagingItems<Article> = mainViewModel.articleListData.collectAsLazyPagingItems()
                val focusManager = LocalFocusManager.current

                Scaffold(
                    topBar = {
                        TopBar(
                            backgroundColor = Color.White
                        )
                    },
                    backgroundColor = colorResource(R.color.backgroundGrey)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    focusManager.clearFocus()
                                }
                            }
                    ){
                        item {
                            Column{

                                // Static Text
                                Text(text = "Today in")

                                // Current Weather details
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                ){

                                    // Text Column
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth(0.75f)
                                    ) {
                                        // City Name
                                        Text(
                                            text = mainViewModel.cityName.value,
                                            style = MaterialTheme.typography.h4
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(0.dp, 0.dp, 8.dp, 0.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ){
                                            Text(
                                                fontSize = 14.sp,
                                                text = "Max ${mainViewModel.tempMax.value} / Min ${mainViewModel.tempMin.value}"
                                            )
                                            Text(
                                                fontSize = 14.sp,
                                                text = "Current ${mainViewModel.temp.value}"
                                            )
                                        }
                                    }

                                    // Icon column
                                    Image(
                                        painter = rememberImagePainter(mainViewModel.iconURL.value),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(128.dp)
                                            .padding(8.dp)
                                    )

                                }

                                // Next 5 Days Weather details
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    elevation = 4.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text( "Next 3 days")
                                        
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ){
                                            mainViewModel.dayList.forEach{
                                                Column {
                                                    Image(
                                                        painter = rememberImagePainter(it.iconURL),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(60.dp)
                                                    )
                                                    Text(
                                                        text = "${it.maxTemp} / ${it.minTemp}",
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                        
                                    }
                                }

                                // Static Text
                                Text(
                                    text = "News Highlights",
                                    style = MaterialTheme.typography.h4,
                                    modifier = Modifier
                                        .padding(0.dp, 24.dp, 0.dp, 0.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                            }
                        }

                        // Recycler view of news
//                        items(lazyArticleItems){ article ->
//                            NewsArticleCard(article = article!!)
//                        }
                        items(mainViewModel.articleList){ article ->
                            NewsArticleCard(article = article)
                        }

                    }

                }
            }
        }
    }

    @ExperimentalAnimationApi
    @Composable
    fun TopBar(backgroundColor: Color) {
        val items = listOf(
            "New York",
            "New Jersey",
            "Raleigh",
            "Cary",
            "Morrisville",
            "Durham"
        )

        val focusManager = LocalFocusManager.current

        // TopBar Row
        Surface(
        color = backgroundColor,
            modifier = Modifier
                .fillMaxWidth(),
            elevation = 24.dp

        ) {

            // Inner Row
            Row(
                modifier = Modifier
                    .padding(8.dp, 4.dp, 8.dp, 4.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(0.dp, 0.dp, 0.dp, 4.dp),
                horizontalArrangement = Arrangement.Start

            ) {

                // Search Icon Button
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        CoroutineScope(Dispatchers.Default).launch {
                            searchOnClick()
                        }

                    },
                    modifier = Modifier
                        .padding(2.dp, 12.dp, 0.dp, 0.dp)

                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Search Box
                AutoCompleteSearch(items, mainViewModel)
            }
        }

    }

    private fun capitalizeCityName(oldName: String): String {
        val words = oldName.lowercase(Locale.getDefault()).split(" ")
        val stringBuilder = StringBuilder()
        for (word in words) {
            val updatedWord = word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            stringBuilder.append(updatedWord).append(" ")
        }
        return stringBuilder.toString()
    }

    private suspend fun searchOnClick() = coroutineScope{

        val apiService = OpenWeatherAPIService.getOpenWeatherAPIService()
        val call: Call<OpenWeatherApiResponse> = apiService.getCityCurrentWeatherData(mainViewModel.query, openWeatherApiKey)
        val forecastCall: Call<ForecastResponse> = apiService.getCityForecastData(mainViewModel.query, openWeatherApiKey)

        forecastCall.enqueue(object : Callback<ForecastResponse?> {
            override fun onResponse(
                call: Call<ForecastResponse?>,
                response: Response<ForecastResponse?>
            ) {
                val statusCode = response.code()
                if (statusCode == 404) {
                    Log.d(TAG, "City not found")
                    mainViewModel.isValidCity.value = false
                } else if (statusCode == 200){
                    val cityForecastResponse: ForecastResponse? = response.body()
                    if (cityForecastResponse != null) {
                        mapForecastResponseToViewModel(cityForecastResponse)
                    }
                    mainViewModel.isValidCity.value = true
                }
                else{
                    Log.d(TAG, "Unknown status code: $statusCode")
                }

            }
            override fun onFailure(
                call: Call<ForecastResponse?>,
                t: Throwable
            ) {
                // Log error since request failed
                Log.d(TAG, "Failed $t")
            }
        })

        call.enqueue(object : Callback<OpenWeatherApiResponse?> {
            override fun onResponse(
                call: Call<OpenWeatherApiResponse?>,
                response: Response<OpenWeatherApiResponse?>
            ) {
                val statusCode = response.code()
                if (statusCode == 404) {
                    Toast.makeText(baseContext, "Invalid City", Toast.LENGTH_SHORT).show()
                    mainViewModel.isValidCity.value = false
                } else if (statusCode == 200){
                    val cityWeatherResponse: OpenWeatherApiResponse? = response.body()
                    if (cityWeatherResponse != null) {
                        mapWeatherResponseToViewModel(cityWeatherResponse)
                    }
                    mainViewModel.isValidCity.value = true
                }
                else{
                    Log.d(TAG, "Unknown status code: $statusCode")
                }
            }
            override fun onFailure(
                call: Call<OpenWeatherApiResponse?>,
                t: Throwable
            ) {
                // Log error since request failed
                Log.d(TAG, "Failed $t")
            }
        })


        // News API calls
        val newsApiService = NewsAPIService.getNewsAPIService()

        val newsCall: Call<NewsAPiResponse> = newsApiService.getArticles(1, 6, mainViewModel.query, newsApiKey)
        newsCall.enqueue(object : Callback<NewsAPiResponse?> {
            override fun onResponse(
                call: Call<NewsAPiResponse?>,
                response: Response<NewsAPiResponse?>
            ) {

                // Process news only if the city is a valid city from the Weather Service
                if(mainViewModel.isValidCity.value) {
                    val statusCode = response.code()
                    if (statusCode == 404) {
                        Toast.makeText(baseContext, "News not found!!!", Toast.LENGTH_SHORT).show()

                    } else if (statusCode == 200) {
                        val newsResponse: NewsAPiResponse? = response.body()
                        if (newsResponse != null) {
                            mapNewsResponseToViewModel(newsResponse)
                        }
                    } else {
                        Log.d(TAG, "Unknown status code: $statusCode")
                    }
                }
            }
            override fun onFailure(
                call: Call<NewsAPiResponse?>,
                t: Throwable
            ) {
                // Log error since request failed
                Log.d(TAG, "Failed $t")
            }
        })
    }
    
    private fun mapWeatherResponseToViewModel(response: OpenWeatherApiResponse){
        val currentWeather = response.weather[0]

        mainViewModel.cityName.value = capitalizeCityName(mainViewModel.query)
        mainViewModel.iconURL.value = iconBaseURL+currentWeather.icon+".png"

        // Convert temperatures to Celsius before updating
        mainViewModel.temp.value = convertKelvinToCelsiusString(response.main.temp)
        mainViewModel.tempMin.value = convertKelvinToCelsiusString(response.main.temp_min)
        mainViewModel.tempMax.value = convertKelvinToCelsiusString(response.main.temp_max)
    }

    private fun mapForecastResponseToViewModel(response: ForecastResponse){
        val allForecastList = response.list

        val jumpMultiplier = 8 // Set this to 1 if you want the next 3 values instead of the next "3 days"

        mainViewModel.dayList.clear()
        for(i in 1..3){
            // Get information after every 24 hours (step of 3 hours * 8)
            val pos = i*jumpMultiplier - 1
            val iconURL = iconBaseURL + allForecastList[pos].weather[0].icon + ".png"

            // Convert temperatures to Celsius before updating
            val minTemp = convertKelvinToCelsiusString(allForecastList[pos].main.temp_min)
            val maxTemp = convertKelvinToCelsiusString(allForecastList[pos].main.temp_max)

            mainViewModel.dayList.add(NextDayWeather(iconURL, minTemp, maxTemp))
        }
    }

    private fun mapNewsResponseToViewModel(response: NewsAPiResponse){
        mainViewModel.articleList.clear()
        for(article in response.articles){
            mainViewModel.articleList.add(article)
        }
    }

    private fun convertKelvinToCelsiusString(k:Double): String{
        return (((k - 273.15) * 10.0).roundToInt() / 10.0).toString()
    }

    private fun setupViewModel() {
        mainViewModel =
            ViewModelProvider(
                this,
                MainViewModelFactory(NewsAPIService.getNewsAPIService())
            )[MainViewModel::class.java]
        mainViewModel.updateNewsApiKey(newsApiKey)
    }

}
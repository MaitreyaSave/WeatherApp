package com.example.weatherapp.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.OpenWeatherAPIService
import com.example.weatherapp.data.response.OpenWeatherApiResponse
import com.example.weatherapp.ui.custom.AutoCompleteSearch
import com.example.weatherapp.ui.main.viewmodel.MainViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt


class MainActivity : ComponentActivity() {
    companion object {
        const val openWeatherApiKey = "7fbefd178fabde87e956d38990bbad5f"
        const val iconBaseURL = "https://openweathermap.org/img/w/"
    }

    private val mainViewModel = MainViewModel()

    @ExperimentalCoilApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                val itemsList = (0..5).toList()

                Scaffold(
                    topBar = {
                        TopBar(
                            backgroundColor = MaterialTheme.colors.primary
                        )
                    },
                    backgroundColor = colorResource(R.color.backgroundGrey)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ){
                        item {
                            Column(
                                // TODO: Check how to remove focus from TextField after clicking on this column
//                            .focusTarget()
                            ) {

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
                                                fontSize = 12.sp,
                                                text = "Max ${mainViewModel.temp_max.value} / Min ${mainViewModel.temp_min.value}"
                                            )
                                            Text(
                                                fontSize = 12.sp,
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
                                        .height(200.dp),
                                    elevation = 10.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text( "Next 5 days")
                                        
                                        LazyRow(){
                                            items(itemsList) {
                                                Text("i:$it")
                                                Spacer(modifier = Modifier.padding(8.dp))
                                            }
                                        }
                                        
                                    }
                                }

                                //
                                // Static Text
                                Text(
                                    text = "News Highlights",
                                    style = MaterialTheme.typography.h4,
                                    modifier = Modifier
                                        .padding(0.dp, 24.dp, 0.dp, 0.dp)
                                )
                            }
                        }

                        // Recycler view of news

                        items(itemsList) {
                            Text("Item is $it")
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

        // TopBar Row
        Surface(
//        color = backgroundColor,
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
                        GlobalScope.launch {
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
        val words = oldName.split(" ")
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
        //
        val apiService = OpenWeatherAPIService.getOpenWeatherAPIService()
        val call: Call<OpenWeatherApiResponse> =
            apiService.getCityCurrentWeatherData(mainViewModel.query, openWeatherApiKey)
        call.enqueue(object : Callback<OpenWeatherApiResponse?> {
            override fun onResponse(
                call: Call<OpenWeatherApiResponse?>,
                response: Response<OpenWeatherApiResponse?>
            ) {
                val statusCode = response.code()
                if (statusCode == 404) {
                    Log.d("ttt", "City not found")
                    Toast.makeText(baseContext, "Invalid City", Toast.LENGTH_SHORT).show()
                } else{
                    // no-op
                }
                val cityWeatherResponse: OpenWeatherApiResponse? = response.body()
                if (cityWeatherResponse != null) {
                    mapResponseToViewModel(cityWeatherResponse)
                }
            }

            override fun onFailure(
                call: Call<OpenWeatherApiResponse?>,
                t: Throwable
            ) {
                // Log error here since request failed
                Log.d("ttt", "Failed $t")
            }
        })


    }
    
    private fun mapResponseToViewModel(response: OpenWeatherApiResponse){
        val currentWeather = response.weather[0]
//        Log.d("ttt", "res: ${currentWeather.description}")
        mainViewModel.cityName.value = capitalizeCityName(mainViewModel.query)
        mainViewModel.iconURL.value = iconBaseURL+currentWeather.icon+".png"

        // Convert temperatures to Celsius before updating
        mainViewModel.temp.value = convertKelvinToCelsiusString(response.main.temp)
        mainViewModel.temp_min.value = convertKelvinToCelsiusString(response.main.temp_min)
        mainViewModel.temp_max.value = convertKelvinToCelsiusString(response.main.temp_max)
    }

    private fun convertKelvinToCelsiusString(k:Double): String{
        return (((k - 273.15) * 10.0).roundToInt() / 10.0).toString()
    }

}
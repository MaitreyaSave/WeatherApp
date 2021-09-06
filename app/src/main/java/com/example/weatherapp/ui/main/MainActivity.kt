package com.example.weatherapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.OpenWeatherAPIService
import com.example.weatherapp.data.response.OpenWeatherApiResponse
import com.example.weatherapp.ui.custom.TextSearchBar
import com.example.weatherapp.ui.custom.autocomplete.AutoCompleteBox
import com.example.weatherapp.ui.custom.autocomplete.utils.AutoCompleteSearchBarTag
import com.example.weatherapp.ui.custom.autocomplete.utils.asAutoCompleteEntities
import com.example.weatherapp.ui.main.MainActivity.Companion.openWeatherApiKey
import com.example.weatherapp.ui.main.viewmodel.MainViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : ComponentActivity() {
    companion object{
        const val openWeatherApiKey = "7fbefd178fabde87e956d38990bbad5f"
    }
    private val mainViewModel = MainViewModel()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Scaffold(
                    topBar = { TopBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        mainViewModel = mainViewModel
                    ) },
                    backgroundColor = Color.Gray
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.White)
                            .padding(16.dp)
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            Text(text = "Today in")
                            Text(
                                text = mainViewModel.cityName.value,
                                style = MaterialTheme.typography.h4
                            )
                        }
                        
                    }
                }
            }
        }
    }
}
//

@ExperimentalAnimationApi
@Composable
fun TopBar(backgroundColor: Color, mainViewModel: MainViewModel) {
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
                    Log.d("ttt", "onclick")
                    val q = mainViewModel.query

                    //
                    val apiService = OpenWeatherAPIService.getOpenWeatherAPIService()
                    val call: Call<OpenWeatherApiResponse> = apiService.getCityCurrentWeatherData(q, openWeatherApiKey)
                    call.enqueue(object : Callback<OpenWeatherApiResponse?> {
                        override fun onResponse(call: Call<OpenWeatherApiResponse?>, response: Response<OpenWeatherApiResponse?>) {
                            val statusCode = response.code()
                            if(statusCode == 404){
                                Log.d("ttt", "City not found")
                            }
                            val user: OpenWeatherApiResponse? = response.body()
                            if (user != null) {
                                Log.d("ttt", "res: ${user.weather[0].description}")
                                mainViewModel.cityName.value = capitalizeCityName(q)
                            }
                        }

                        override fun onFailure(call: Call<OpenWeatherApiResponse?>, t: Throwable) {
                            // Log error here since request failed
                            Log.d("ttt","Failed $t")
                        }
                    })

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
            AutoCompleteValue(items, mainViewModel)
        }
    }

}

fun capitalizeCityName(oldName: String):String {
    val words = oldName.split(" ")
    val stringBuilder = StringBuilder()
    for( word in words){
        val updatedWord = word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        stringBuilder.append(updatedWord).append(" ")
    }
    return stringBuilder.toString()
}

@ExperimentalAnimationApi
@Composable
fun AutoCompleteValue(items: List<String>, mainViewModel: MainViewModel) {

    val autoCompleteEntities = items.asAutoCompleteEntities(
        filter = { item, query ->
            item.lowercase(Locale.getDefault())
                .startsWith(query.lowercase(Locale.getDefault()))
        }
    )

    AutoCompleteBox(
        items = autoCompleteEntities,
        itemContent = { item ->
            ValueAutoCompleteItem(item.value)
        }
    ) {
        var value by remember { mutableStateOf("") }
        val view = LocalView.current

        onItemSelected { item ->
            value = item.value
            //filter(value)
            view.clearFocus()
        }

        TextSearchBar(
            modifier = Modifier.testTag(AutoCompleteSearchBarTag),
            value = value,
            label = "Search city",
            onDoneActionClick = {
                view.clearFocus()
            },
            onClearClick = {
                value = ""
                mainViewModel.onQueryChanged("")
                //filter(value)
                view.clearFocus()
            },
            onFocusChanged = { focusState ->
                isSearching = focusState.hasFocus
            },
            onValueChanged = { query ->
                value = query
                mainViewModel.onQueryChanged(query)
                //filter(value)
            }
        )
    }
}

@Composable
fun ValueAutoCompleteItem(item: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = item, style = MaterialTheme.typography.subtitle2)
    }
}



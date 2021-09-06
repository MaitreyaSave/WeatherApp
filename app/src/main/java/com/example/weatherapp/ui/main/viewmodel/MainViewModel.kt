package com.example.weatherapp.ui.main.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var query = ""
    val cityName = mutableStateOf("")
    val iconCode = mutableStateOf("")

    fun onQueryChanged(query: String){
        this.query = query
    }
}
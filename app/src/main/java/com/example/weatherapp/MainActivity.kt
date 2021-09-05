package com.example.weatherapp

import android.os.Bundle
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
import com.example.weatherapp.ui.components.TextSearchBar
import com.example.weatherapp.ui.components.autocomplete.AutoCompleteBox
import com.example.weatherapp.ui.components.autocomplete.utils.AutoCompleteSearchBarTag
import com.example.weatherapp.ui.components.autocomplete.utils.asAutoCompleteEntities
import com.example.weatherapp.ui.theme.WeatherAppTheme
import java.util.*


class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Scaffold(
                    topBar = { TopBar(
                        backgroundColor = MaterialTheme.colors.primaryVariant
                    ) },
                    backgroundColor = Color.Gray
                ) {

                }
            }
        }
    }
}
//

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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)

    ) {

        // Inner Row
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(Color.White)
                .padding(0.dp, 0.dp, 0.dp, 8.dp)

        ) {

            // Search Icon Button
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(8.dp, 12.dp, 8.dp, 0.dp)

            ) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Search Box
            AutoCompleteValue(items)
        }
    }

}


@ExperimentalAnimationApi
@Composable
fun AutoCompleteValue(items: List<String>) {

//    val items = listOf(
//        "Paulo Pereira",
//        "Daenerys Targaryen",
//        "Jon Snow",
//        "Sansa Stark",
//    )
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
            filter(value)
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
                filter(value)
                view.clearFocus()
            },
            onFocusChanged = { focusState ->
                isSearching = focusState.hasFocus
            },
            onValueChanged = { query ->
                value = query
                filter(value)
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



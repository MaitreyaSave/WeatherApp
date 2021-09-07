package com.example.weatherapp.ui.custom.newscard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.weatherapp.data.response.news.Article

@ExperimentalCoilApi
@Composable
fun NewsArticleCard(
    article: Article
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /*TODO: Start a new activity to load the selected News Article*/ }
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            // Article thumbnail
            val imageURL = article.urlToImage ?: "https://dummyimage.com/200/b8b8b8/000.png&text=No+image+URL+found!"
            Image(
                painter = rememberImagePainter(imageURL),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(80.dp)
                    .padding(8.dp)
            )

            // Text details
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                // Article Title
                Text(
                    text = article.title
                )

                // Article Source
                Text(
                    text = article.source.name,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}


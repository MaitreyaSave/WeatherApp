<h1 align="center">Weather App Sample</h1>

<p align="center">
  <a href="https://github.com/MaitreyaSave"><img alt="Profile" src="https://badges.aleen42.com/src/github.svg"/></a> 
</p>

## Description
This is a sample (experimental) project that worked on over a long weekend. 
The user can enter a city name in the search bar to find weather and news details for that city.

I have used `Jetpack Compose` (because finally we have a stable build!!!) for its UI and I used `Retrofit` for handling API calls.
There is also a really cool plugin in AndroidStudio called `JSON To Kotlin Class` which helps you to create Data classes from a sample JSON file :D

I noticed that the [5-Days forecast API](https://openweathermap.org/forecast5) call gives forecasts of every 3 hours. So, technically getting the next day's forecast would be to get the 8th element of the list.
That is why, to get the forecast for the next **3 days** I used the `8th, 16th and 24th` element in the list.
I also noticed that the actual data (min and max temp) that we get from that API call sometimes becomes duplicate (min temp is same as max temp) after the **3rd element**.

So, if you just want the next 3 elements (instead of the 8th, 16th and 24th), please find the variable `jumpMultiplier` and set it to the value **1**.
This is just an explanation in case you were wondering as to why the min temp and max temp values appear the same on screen.



## Future Work?
1 - I tried implementing Flow and Paging 3 (with Retrofit + Moshi) for loading the news API responses. 
But, for some reason Flow did not work well Compose :( 
It is also possible that I did not implement the API calls properly while using Flow. (Using a Response<T> instead of a Call<T>)
There is some commented code in the MainActivity related to this.

2 - I also tried adding a Auto-suggest dropdown for the search bar. I do not know what went wrong there.

I might consider redoing these parts on some other weekend!

## Author
Hello, I am Maitreya :)

I am a professional Android Developer and I also develop Android apps for fun :D
If you want to discuss/collaborate on an idea for a cool app, drop me an email: msave@ncsu.edu

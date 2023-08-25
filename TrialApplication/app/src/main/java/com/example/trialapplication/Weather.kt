package com.example.trialapplication

import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
//import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import com.google.gson.Gson
import io.ktor.client.statement.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Weather : ComponentActivity() {

    private fun checkWeatherCode(weatherCode: Int): String{
        var weatherStatus = "Unknown Weather"
        when (weatherCode){
            0 -> { weatherStatus = "Clear Sky"}
            1 -> { weatherStatus = "Mainly Clear"}
            2 -> { weatherStatus = "Partly Cloudy"}
            3 -> { weatherStatus = "Overcast"}
            45 -> { weatherStatus = "Fog"}
            48 -> { weatherStatus = "Depositing Rime Fog"}
            51 -> { weatherStatus = "Light Drizzle"}
            53 -> { weatherStatus = "Moderate Drizzle"}
            55 -> { weatherStatus = "Dense Drizzle"}
            61 -> { weatherStatus = "Slight Rain"}
            63 -> { weatherStatus = "Moderate Rain"}
            65 -> { weatherStatus = "Heavy Rain"}
            66 -> { weatherStatus = "Light Freezing Rain"}
            67 -> { weatherStatus = "Heavy Freezing Rain"}
            71 -> { weatherStatus = "Slight Snow Fall"}
            73 -> { weatherStatus = "Moderate Snow Fall"}
            75 -> { weatherStatus = "Heavy Snow Fall"}
            77 -> { weatherStatus = "Snow Grains"}
            80 -> { weatherStatus = "Slight Rain Showers"}
            81 -> { weatherStatus = "Moderate Rain Showers"}
            82 -> { weatherStatus = "Violent Rain Showers"}
            85 -> { weatherStatus = "Slight Snow Showers"}
            86 -> { weatherStatus = "Heavy Snow Showers"}
            95 -> { weatherStatus = "Thunderstorm"}
            96 -> { weatherStatus = "Thunderstorm with slight hail"}
            99 -> { weatherStatus = "Thunderstorm with heavy hail"}
        }
        return weatherStatus
    }

    private suspend fun retrieveData(){
        val resp: Response
        val response: HttpResponse
        try {
            println("Successful till here@!@")
            withContext(Dispatchers.IO) {
                val url =
                    "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=13.0878&" +
                            "longitude=80.2785&" +
                            "timezone=auto&" +
                            "current_weather=true&" +
                            "daily=sunrise,sunset,uv_index_max,uv_index_clear_sky_max," +
                            "precipitation_sum,windspeed_10m_max,windgusts_10m_max," +
                            "winddirection_10m_dominant,temperature_2m_max,temperature_2m_min&" +
                            "forecast_days=3"
                response = HttpClient(CIO).request(url) {
                    method = HttpMethod.Get
                }
                resp = Gson().fromJson(response.readText(), Response::class.java)
//                resp = Gson().fromJson(response.bodyAsText(), Response::class.java)
            }
            println("Successful till here too@!@")
            withContext(Dispatchers.Main) {
                // Current Weather
                val temperature = findViewById<TextView>(R.id.temperature)
                val windSpeed = findViewById<TextView>(R.id.windSpeed)
                val windDirection = findViewById<TextView>(R.id.windDirection)
                val weatherCode = findViewById<TextView>(R.id.weatherCode)
                val isDay = findViewById<TextView>(R.id.isDay)
                val updatedTime = findViewById<TextView>(R.id.updatedTime)
                temperature.text = resp.current_weather.temperature.toString()
                windSpeed.text = resp.current_weather.windspeed.toString()
                windDirection.text = resp.current_weather.winddirection.toString()

                // Get Weather Status from Weather Code
                weatherCode.text = checkWeatherCode(resp.current_weather.weathercode)

                // Convert 1/0 to Yes/No
                val yesString = "Yes"
                val noString = "No"
                if (resp.current_weather.is_day == 1)
                    isDay.text = yesString
                else
                    isDay.text = noString

                updatedTime.text = resp.current_weather.time.replace("T", ", ")
                val currentWeatherTextView = findViewById<TextView>(R.id.currentWeatherTextView)
                val currentWeatherContent =
                    findViewById<HorizontalScrollView>(R.id.currentWeatherContent)
                currentWeatherTextView.visibility = View.INVISIBLE
                currentWeatherContent.visibility = View.VISIBLE

                // Forecast
                findViewById<TextView>(R.id.temperatureMaxValue1).text =
                    resp.daily.temperature_2m_max[0].toString()
                findViewById<TextView>(R.id.temperatureMaxValue2).text =
                    resp.daily.temperature_2m_max[1].toString()
                findViewById<TextView>(R.id.temperatureMaxValue3).text =
                    resp.daily.temperature_2m_max[2].toString()

                findViewById<TextView>(R.id.temperatureMinValue1).text =
                    resp.daily.temperature_2m_min[0].toString()
                findViewById<TextView>(R.id.temperatureMinValue2).text =
                    resp.daily.temperature_2m_min[1].toString()
                findViewById<TextView>(R.id.temperatureMinValue3).text =
                    resp.daily.temperature_2m_min[2].toString()

                findViewById<TextView>(R.id.sunriseValue1).text =
                    resp.daily.sunrise[0].split("T")[1]
                findViewById<TextView>(R.id.sunriseValue2).text =
                    resp.daily.sunrise[1].split("T")[1]
                findViewById<TextView>(R.id.sunriseValue3).text =
                    resp.daily.sunrise[2].split("T")[1]

                findViewById<TextView>(R.id.sunsetValue1).text = resp.daily.sunset[0].split("T")[1]
                findViewById<TextView>(R.id.sunsetValue2).text = resp.daily.sunset[1].split("T")[1]
                findViewById<TextView>(R.id.sunsetValue3).text = resp.daily.sunset[2].split("T")[1]

                findViewById<TextView>(R.id.windSpeedValue1).text =
                    resp.daily.windspeed_10m_max[0].toString()
                findViewById<TextView>(R.id.windSpeedValue2).text =
                    resp.daily.windspeed_10m_max[1].toString()
                findViewById<TextView>(R.id.windSpeedValue3).text =
                    resp.daily.windspeed_10m_max[2].toString()

                findViewById<TextView>(R.id.windGustsValue1).text =
                    resp.daily.windgusts_10m_max[0].toString()
                findViewById<TextView>(R.id.windGustsValue2).text =
                    resp.daily.windgusts_10m_max[1].toString()
                findViewById<TextView>(R.id.windGustsValue3).text =
                    resp.daily.windgusts_10m_max[2].toString()

                findViewById<TextView>(R.id.windDirectionValue1).text =
                    resp.daily.winddirection_10m_dominant[0].toString()
                findViewById<TextView>(R.id.windDirectionValue2).text =
                    resp.daily.winddirection_10m_dominant[1].toString()
                findViewById<TextView>(R.id.windDirectionValue3).text =
                    resp.daily.winddirection_10m_dominant[2].toString()

                findViewById<TextView>(R.id.precipitationValue1).text =
                    resp.daily.precipitation_sum[0].toString()
                findViewById<TextView>(R.id.precipitationValue2).text =
                    resp.daily.precipitation_sum[1].toString()
                findViewById<TextView>(R.id.precipitationValue3).text =
                    resp.daily.precipitation_sum[2].toString()

                findViewById<TextView>(R.id.uvIndexClearSkyMaxValue1).text =
                    resp.daily.uv_index_clear_sky_max[0].toString()
                findViewById<TextView>(R.id.uvIndexClearSkyMaxValue2).text =
                    resp.daily.uv_index_clear_sky_max[1].toString()
                findViewById<TextView>(R.id.uvIndexClearSkyMaxValue3).text =
                    resp.daily.uv_index_clear_sky_max[2].toString()

                findViewById<TextView>(R.id.uvIndexMaxValue1).text =
                    resp.daily.uv_index_max[0].toString()
                findViewById<TextView>(R.id.uvIndexMaxValue2).text =
                    resp.daily.uv_index_max[1].toString()
                findViewById<TextView>(R.id.uvIndexMaxValue3).text =
                    resp.daily.uv_index_max[2].toString()

                findViewById<TextView>(R.id.dayValue1).text = resp.daily.time[0]
                findViewById<TextView>(R.id.dayValue2).text = resp.daily.time[1]
                findViewById<TextView>(R.id.dayValue3).text = resp.daily.time[2]

                findViewById<TextView>(R.id.forecastTextView).visibility = View.INVISIBLE
                findViewById<HorizontalScrollView>(R.id.forecastContent).visibility = View.VISIBLE
                println("not here though@!@")
            }
        }catch (e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = "Check your Internet Connection."
                findViewById<TextView>(R.id.currentWeatherTextView).text = failedMessage
                findViewById<TextView>(R.id.forecastTextView).text = failedMessage
            }
        }
        catch (e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = "Failed to load data."
                println("Exception !@!@!")
                println(e.message)
                println(e)
                findViewById<TextView>(R.id.currentWeatherTextView).text = failedMessage
                findViewById<TextView>(R.id.forecastTextView).text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)
        lifecycleScope.launch{
            retrieveData()
        }
    }
}
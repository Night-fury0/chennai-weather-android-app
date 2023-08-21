package com.example.trialapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.coroutines.runBlocking
import com.example.trialapplication.Response
import com.google.gson.Gson

class Weather : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)
        val forecastContent = findViewById<TextView>(R.id.forecastContent)
        val currentWeatherContent = findViewById<TextView>(R.id.currentWeatherContent)
        val getDataButton = findViewById<Button>(R.id.getData)
        getDataButton.setOnClickListener {
            runBlocking {
                val client = HttpClient(CIO)
                val url =
                    "https://api.open-meteo.com/v1/forecast?latitude=13.0878&longitude=80.2785&timezone=auto&current_weather=true&daily=sunrise,sunset,uv_index_max,uv_index_clear_sky_max,precipitation_sum,windspeed_10m_max,windgusts_10m_max,winddirection_10m_dominant,temperature_2m_max,temperature_2m_min&forecast_days=3"
                val response: HttpResponse = client.request(url) {
                    method = HttpMethod.Get
                }
                println(response.bodyAsText())
                val resp : Response = Gson().fromJson(response.bodyAsText(), Response::class.java)
                println(resp.toString())
                currentWeatherContent.text = resp.current_weather.toString()
                forecastContent.text = resp.daily.toString()
                client.close()
            }
        }
    }
}
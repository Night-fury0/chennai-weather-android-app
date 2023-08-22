package com.example.trialapplication

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Weather : ComponentActivity() {

    suspend fun retrieveData(){
        val resp: Response
        val response: HttpResponse
        withContext(Dispatchers.IO) {
            val url =
                "https://api.open-meteo.com/v1/forecast?latitude=13.0878&longitude=80.2785&timezone=auto&current_weather=true&daily=sunrise,sunset,uv_index_max,uv_index_clear_sky_max,precipitation_sum,windspeed_10m_max,windgusts_10m_max,winddirection_10m_dominant,temperature_2m_max,temperature_2m_min&forecast_days=3"
            response = HttpClient(CIO).request(url) {
                method = HttpMethod.Get
            }
            resp = Gson().fromJson(response.bodyAsText(), Response::class.java)
        }
        withContext(Dispatchers.Main) {
            val forecastContent = findViewById<TextView>(R.id.forecastContent)
            val currentWeatherContent = findViewById<TextView>(R.id.currentWeatherContent)
            currentWeatherContent.text = resp.current_weather.toString()
            forecastContent.text = resp.daily.toString()
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
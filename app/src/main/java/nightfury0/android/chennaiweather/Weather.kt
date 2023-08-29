package nightfury0.android.chennaiweather

import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

    private lateinit var currentWeatherTextView: TextView
    private lateinit var forecastTextView: TextView
    private fun checkWeatherCode(weatherCode: Int): String{
        var weatherStatus = "Undefined Weather"
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
            withContext(Dispatchers.Main){
                findViewById<HorizontalScrollView>(R.id.forecastContent).visibility = View.INVISIBLE
                findViewById<HorizontalScrollView>(R.id.currentWeatherContent).visibility = View.INVISIBLE
                currentWeatherTextView.text = resources.getString(R.string.loading_text)
                forecastTextView.text = resources.getString(R.string.loading_text)
                currentWeatherTextView.visibility = View.VISIBLE
                forecastTextView.visibility = View.VISIBLE
            }
            withContext(Dispatchers.IO) {
                val url = getString(R.string.weather_url)
                response = HttpClient(CIO).request(url) {
                    method = HttpMethod.Get
                }
                resp = Gson().fromJson(response.readText(), Response::class.java)
//                resp = Gson().fromJson(response.bodyAsText(), Response.kt::class.java)
            }
            withContext(Dispatchers.Main) {
                // Current Weather
                val currentWeatherValues = listOf(
                    resources.getString(R.string.temperature_2m),
                    resp.current_weather.temperature.toString(),
                    resources.getString(R.string.wind_speed),
                    resp.current_weather.windspeed.toString(),
                    resources.getString(R.string.wind_direction),
                    resp.current_weather.winddirection.toString(),
                    resources.getString(R.string.weather_status),
                    checkWeatherCode(resp.current_weather.weathercode),
                    resources.getString(R.string.is_day),
                    if (resp.current_weather.is_day == 1) resources.getString(R.string.yes_string) else resources.getString(R.string.no_string),
                    resources.getString(R.string.updated_time),
                    resp.current_weather.time.replace("T", ", ")
                )
                val currentWeatherTable = findViewById<TableLayout>(R.id.currentWeatherTable)
                currentWeatherTable.removeAllViews()
                Templates().formTableForCurrentWeather(this@Weather, currentWeatherTable, currentWeatherValues, 2)

                findViewById<HorizontalScrollView>(R.id.currentWeatherContent).visibility = View.VISIBLE
                currentWeatherTextView.visibility = View.INVISIBLE

                // Forecast
                val headerValues = listOf(
                    resources.getString(R.string.date),
                    resp.daily.time[0],
                    resp.daily.time[1],
                    resp.daily.time[2],
                )
                val values = listOf(
                    resources.getString(R.string.sunrise),
                    resp.daily.sunrise[0].split("T")[1],
                    resp.daily.sunrise[1].split("T")[1],
                    resp.daily.sunrise[2].split("T")[1],
                    resources.getString(R.string.sunset),
                    resp.daily.sunset[0].split("T")[1],
                    resp.daily.sunset[1].split("T")[1],
                    resp.daily.sunset[2].split("T")[1],
                    resources.getString(R.string.uv_index_max),
                    resp.daily.uv_index_max[0].toString(),
                    resp.daily.uv_index_max[1].toString(),
                    resp.daily.uv_index_max[2].toString(),
                    resources.getString(R.string.uv_index_clear_sky_max),
                    resp.daily.uv_index_clear_sky_max[0].toString(),
                    resp.daily.uv_index_clear_sky_max[1].toString(),
                    resp.daily.uv_index_clear_sky_max[2].toString(),
                    resources.getString(R.string.precipitation_sum),
                    resp.daily.precipitation_sum[0].toString(),
                    resp.daily.precipitation_sum[1].toString(),
                    resp.daily.precipitation_sum[2].toString(),
                    resources.getString(R.string.wind_speed_max),
                    resp.daily.windspeed_10m_max[0].toString(),
                    resp.daily.windspeed_10m_max[1].toString(),
                    resp.daily.windspeed_10m_max[2].toString(),
                    resources.getString(R.string.wind_gusts_max),
                    resp.daily.windgusts_10m_max[0].toString(),
                    resp.daily.windgusts_10m_max[1].toString(),
                    resp.daily.windgusts_10m_max[2].toString(),
                    resources.getString(R.string.wind_direction_dominant),
                    resp.daily.winddirection_10m_dominant[0].toString(),
                    resp.daily.winddirection_10m_dominant[1].toString(),
                    resp.daily.winddirection_10m_dominant[2].toString(),
                    resources.getString(R.string.temperature_2m_max),
                    resp.daily.temperature_2m_max[0].toString(),
                    resp.daily.temperature_2m_max[1].toString(),
                    resp.daily.temperature_2m_max[2].toString(),
                    resources.getString(R.string.temperature_2m_min),
                    resp.daily.temperature_2m_min[0].toString(),
                    resp.daily.temperature_2m_min[1].toString(),
                    resp.daily.temperature_2m_min[2].toString())
                val tableLayout = findViewById<TableLayout>(R.id.forecastTable)
                Templates().formTableFromList(this@Weather, tableLayout, headerValues, values)

                forecastTextView.visibility = View.INVISIBLE
                findViewById<HorizontalScrollView>(R.id.forecastContent).visibility = View.VISIBLE
            }
        }catch (e:java.nio.channels.UnresolvedAddressException){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_internet_failure)
                currentWeatherTextView.text = failedMessage
                forecastTextView.text = failedMessage
            }
        }
        catch (e:Exception){
            withContext(Dispatchers.Main){
                val failedMessage = getString(R.string.error_unable_to_retrieve)
                println("Exception !@!@!")
                println(e.message)
                println(e)
                currentWeatherTextView.text = failedMessage
                forecastTextView.text = failedMessage
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)

        currentWeatherTextView = findViewById(R.id.currentWeatherTextView)
        forecastTextView = findViewById(R.id.forecastTextView)

        lifecycleScope.launch{
            retrieveData()
        }

        val weatherSwipeRefresh = findViewById<SwipeRefreshLayout>(R.id.weatherSwipeRefresh)
        weatherSwipeRefresh.setOnRefreshListener {
            lifecycleScope.launch{
                retrieveData()
            }
            weatherSwipeRefresh.isRefreshing = false
        }
    }
}
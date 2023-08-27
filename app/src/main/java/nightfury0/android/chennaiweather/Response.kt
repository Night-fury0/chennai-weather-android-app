package nightfury0.android.chennaiweather

data class Response(
	val latitude: Double,
	val longitude: Double,
	val generationTime: Double,
	val timezone: String,
	val elevation: Double,
	val current_weather: CurrentWeather,
	val daily_units: DailyUnits,
	val daily: Daily
)

data class CurrentWeather(
	val temperature: Double,
	val windspeed: Double,
	val winddirection: Int,
	val weathercode: Int,
	val is_day: Int,
	val time: String
)

data class DailyUnits(
	val time: String,
	val sunrise: String,
	val sunset: String,
	val uv_index_max: String,
	val uv_index_clear_sky_max: String,
	val precipitation_sum: String,
	val windspeed_10m_max: String,
	val windgusts_10m_max: String,
	val winddirection_10m_dominant: String,
	val temperature_2m_max: String,
	val temperature_2m_min: String
)

data class Daily(
	val time: List<String>,
	val sunrise: List<String>,
	val sunset: List<String>,
	val uv_index_max: List<Double>,
	val uv_index_clear_sky_max: List<Double>,
	val precipitation_sum: List<Double>,
	val windspeed_10m_max: List<Double>,
	val windgusts_10m_max: List<Double>,
	val winddirection_10m_dominant: List<Int>,
	val temperature_2m_max: List<Double>,
	val temperature_2m_min: List<Double>
)


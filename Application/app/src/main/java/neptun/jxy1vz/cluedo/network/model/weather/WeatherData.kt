package neptun.jxy1vz.cluedo.network.model.weather

data class WeatherData (
    var coord: Coord,
    var weather: List<Weather>?,
    var base: String,
    var main: MainWeatherData?,
    var visibility: Int,
    var wind: Wind?,
    var clouds: Cloud,
    var dt: Int,
    var sys: Sys,
    var timezone: Int,
    var id: Int,
    var name: String,
    var cod: Int
)

data class Weather (
    val id: Long,
    val main: String?,
    val description: String?,
    val icon: String?
)

data class MainWeatherData (
    val temp: Float,
    val pressure: Float,
    val humidity: Float,
    val temp_min: Float,
    val temp_max: Float
)

data class Coord (
    var lon: Float,
    var lat: Float
)

data class Cloud (
    var all: Int
)

data class Sys (
    var type: Int,
    var id: Int,
    var country: String,
    var sunrise: Int,
    var sunset: Int
)

class Wind (
    val speed: Float,
    val deg: Float
)
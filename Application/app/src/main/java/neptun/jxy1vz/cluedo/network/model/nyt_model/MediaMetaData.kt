package neptun.jxy1vz.cluedo.network.model.nyt_model

import com.squareup.moshi.Json

data class MediaMetaData(
    @Json(name = "url")
    val url: String,
    @Json(name = "format")
    val format: String,
    @Json(name = "height")
    val height: Int,
    @Json(name = "width")
    val width: Int
)
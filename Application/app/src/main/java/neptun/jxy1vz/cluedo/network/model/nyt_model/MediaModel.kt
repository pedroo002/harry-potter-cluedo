package neptun.jxy1vz.cluedo.network.model.nyt_model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class MediaModel(
    @Json(name = "media-metadata") val media_metadata: List<MediaMetaData>
)
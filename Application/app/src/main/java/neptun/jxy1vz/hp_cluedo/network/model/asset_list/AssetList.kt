package neptun.jxy1vz.hp_cluedo.network.model.asset_list

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssetList(
    @Json(name = "file_names")
    val fileNames: List<Path>
)

@JsonClass(generateAdapter = true)
data class Path(
    @Json(name = "path")
    val path: String
)

@JsonClass(generateAdapter = true)
data class AssetCount(
    @Json(name = "count")
    val count: Int
)
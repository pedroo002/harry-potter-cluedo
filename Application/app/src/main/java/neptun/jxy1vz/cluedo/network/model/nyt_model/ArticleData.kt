package neptun.jxy1vz.cluedo.network.model.nyt_model

import com.squareup.moshi.Json

data class ArticleData(
    @Json(name = "results")
    val results: List<ArticleProperties>
)
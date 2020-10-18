package neptun.jxy1vz.cluedo.network.model.nyt_model

import com.squareup.moshi.Json
import neptun.jxy1vz.cluedo.network.model.nyt_model.domain.Article

data class ArticleProperties(
    @Json(name = "url")
    val url: String,
    @Json(name = "section")
    val section: String,
    @Json(name = "byline")
    val byline: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "published_date")
    val published_date: String,
    @Json(name = "id")
    val id: Long,
    @Json(name = "media")
    val media: List<MediaModel>
)

fun ArticleProperties.toDomainModel() = Article(this.id, this.title, this.byline, this.published_date, this.url, this.media[0].media_metadata[0].url)
package neptun.jxy1vz.cluedo.network.model.nyt_model.domain

data class Article(
    var id: Long = 0,
    var title: String = "",
    var byline: String = "",
    var published_date: String = "",
    var url: String = "",
    var imageUrl: String = ""
)
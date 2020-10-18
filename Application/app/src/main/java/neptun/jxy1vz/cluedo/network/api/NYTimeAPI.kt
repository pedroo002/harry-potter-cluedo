package neptun.jxy1vz.cluedo.network.api

import neptun.jxy1vz.cluedo.network.model.nyt_model.ArticleData
import retrofit2.Call
import retrofit2.http.GET

interface NYTimeAPI {

    @GET("all-sections/7.json")
    suspend fun getNews(): Call<ArticleData>
}
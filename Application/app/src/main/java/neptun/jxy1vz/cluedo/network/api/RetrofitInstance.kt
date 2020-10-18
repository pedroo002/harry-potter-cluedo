package neptun.jxy1vz.cluedo.network.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        private const val URL = "https://pedro.sch.bme.hu/"

        private const val NYT_URL = "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/"
        private const val API_KEY = "begfaIrtSHC2sZUGsVSpixxUXDuKGotG"

        private const val SERVICE_URL = "https://api.openweathermap.org"
        const val SERVICE_KEY = "279874a3b212b124f9541170be2a217c"

        val weather: WeatherApi by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(SERVICE_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(WeatherApi::class.java)
        }

        val nytimes: NYTimeAPI by lazy {
            val okhttpclient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val originalUrl = request.url()
                    val url = originalUrl.newBuilder().addQueryParameter("api-key", API_KEY).build()
                    val requestBuilder = request.newBuilder().url(url)
                    chain.proceed(requestBuilder.build())
                }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val builder = Retrofit.Builder()
                .baseUrl(NYT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addCallAdapterFactory(SimpleCallAdapterFactory.create())

            val retrofit = builder
                .client(okhttpclient.build())
                .build()
            retrofit.create(NYTimeAPI::class.java)
        }

        val cluedo: CluedoApi by lazy {
            val interceptor = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", UUID.randomUUID().toString())
                        .build()
                    chain.proceed(newRequest)
                }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val builder = Retrofit.Builder()
                .baseUrl(URL)
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(SimpleCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))

            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(CluedoApi::class.java)
        }
    }
}
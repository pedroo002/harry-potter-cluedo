package neptun.jxy1vz.cluedo.network.api

import neptun.jxy1vz.cluedo.network.call_adapter.SimpleCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {
        private const val URL = "https://pedro.sch.bme.hu/"

        val retrofit: ApiService by lazy {
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

            val builder = Retrofit.Builder()
                .baseUrl(URL)
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())

            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
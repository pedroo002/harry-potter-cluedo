package neptun.jxy1vz.hp_cluedo.network.api

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import neptun.jxy1vz.hp_cluedo.BuildConfig
import neptun.jxy1vz.hp_cluedo.R
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.tls.HandshakeCertificates
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitInstance private constructor() {

    companion object {
        private const val URL = "https://pedro.sch.bme.hu/"

        private lateinit var retrofit: RetrofitInstance
        private lateinit var context: Context

        fun getInstance(c: Context): RetrofitInstance {
            return if (this::retrofit.isInitialized)
                retrofit
            else {
                context = c
                retrofit = RetrofitInstance()
                retrofit
            }
        }
    }

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_3)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .allEnabledCipherSuites()
        .allEnabledTlsVersions()
        .build()

    val cluedo: CluedoApi by lazy {
        val cf = CertificateFactory.getInstance("X.509")
        val cert = cf.generateCertificate(context.resources.openRawResource(R.raw.certificate))

        val certificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(cert as X509Certificate)
            .build()

        val httpClient = OkHttpClient.Builder()
            .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager)
            .connectionSpecs(Collections.singletonList(spec))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(interceptor)
        }

        val builder = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))

        val retrofit = builder
            .client(httpClient.build())
            .build()
        retrofit.create(CluedoApi::class.java)
    }
}
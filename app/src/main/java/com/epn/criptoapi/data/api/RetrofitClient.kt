package com.epn.criptoapi.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton para configurar y obtener la instancia de Retrofit
 *
 * - Evita crear múltiples instancias de Retrofit
 * - Reutiliza conexiones HTTP
 * - Mejora el rendimiento de la app
 */
object RetrofitClient {

    /**
     * Configuración del interceptor de logs
     * Nos permite ver las peticiones HTTP en el Logcat
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Configuración del cliente HTTP con OkHttp
     * - Timeouts para evitar esperas infinitas
     * - Interceptor de logs para debugging
     */
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Instancia de Retrofit configurada
     * - Base URL de CoinGecko
     * - Conversor Gson para JSON -> Objetos Kotlin
     * - Cliente OkHttp personalizado
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(CoinGeckoApi.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Instancia del servicio API
     * Uso: RetrofitClient.api.getCryptoList(...)
     */
    val api: CoinGeckoApi = retrofit.create(CoinGeckoApi::class.java)
}

package com.epn.criptoapi.data.api

import com.epn.criptoapi.data.model.Crypto
import com.epn.criptoapi.data.model.CryptoDetail
import com.epn.criptoapi.data.model.TrendingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de la API de CoinGecko
 * Documentación: https://docs.coingecko.com/v3.0.1/reference/introduction
 */
interface CoinGeckoApi {

    /**
     * Obtiene la lista de criptomonedas con información de mercado
     */
    @GET("coins/markets")
    suspend fun getCryptoList(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 50,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false,
        @Query("x_cg_demo_api_key") apiKey: String
    ): List<Crypto>

    /**
     * Busca criptomonedas por nombre o símbolo
     */
    @GET("coins/markets")
    suspend fun searchCrypto(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("ids") ids: String,
        @Query("x_cg_demo_api_key") apiKey: String
    ): List<Crypto>

    /**
     * 🔥 Obtiene las criptomonedas en tendencia (trending)
     * Las más buscadas en las últimas 24 horas
     */
    @GET("search/trending")
    suspend fun getTrendingCryptos(
        @Query("x_cg_demo_api_key") apiKey: String
    ): TrendingResponse

    /**
     * 📈 Obtiene el detalle completo de una criptomoneda
     * @param id ID de la crypto (ej: "bitcoin", "ethereum")
     */
    @GET("coins/{id}")
    suspend fun getCryptoDetail(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("x_cg_demo_api_key") apiKey: String
    ): CryptoDetail

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}

package com.epn.criptoapi.data.repository

import com.epn.criptoapi.data.api.RetrofitClient
import com.epn.criptoapi.data.model.Crypto
import com.epn.criptoapi.data.model.CryptoDetail
import com.epn.criptoapi.data.model.TrendingCoin

/**
 * Repositorio que actúa como fuente única de verdad para los datos
 *
 * Patrón Repository:
 * - Abstrae la fuente de datos (API, base de datos, caché)
 * - Facilita testing y mantenimiento
 * - El ViewModel no necesita saber de dónde vienen los datos
 */
class CryptoRepository {

    // Tu API Key de CoinGecko
    private val apiKey = "CG-PSexpCVrQvFZfVGjEKjnfXWz"

    /**
     * Obtiene la lista de criptomonedas más populares
     */
    suspend fun getCryptoList(page: Int = 1): Result<List<Crypto>> {
        return try {
            val response = RetrofitClient.api.getCryptoList(
                vsCurrency = "usd",
                order = "market_cap_desc",
                perPage = 50,
                page = page,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca criptomonedas específicas por ID
     */
    suspend fun searchCrypto(ids: String): Result<List<Crypto>> {
        return try {
            val response = RetrofitClient.api.searchCrypto(
                vsCurrency = "usd",
                ids = ids,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 🔥 Obtiene las criptomonedas en tendencia
     */
    suspend fun getTrendingCryptos(): Result<List<TrendingCoin>> {
        return try {
            val response = RetrofitClient.api.getTrendingCryptos(apiKey = apiKey)
            val trendingCoins = response.coins.map { it.item }
            Result.success(trendingCoins)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 📈 Obtiene el detalle completo de una criptomoneda
     */
    suspend fun getCryptoDetail(id: String): Result<CryptoDetail> {
        return try {
            val response = RetrofitClient.api.getCryptoDetail(
                id = id,
                apiKey = apiKey
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

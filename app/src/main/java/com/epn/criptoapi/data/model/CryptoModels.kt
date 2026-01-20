package com.epn.criptoapi.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para una criptomoneda
 * Representa la respuesta de la API de CoinGecko /coins/markets
 */
data class Crypto(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("market_cap")
    val marketCap: Long,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int?,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,

    @SerializedName("total_volume")
    val totalVolume: Long?,

    @SerializedName("high_24h")
    val high24h: Double?,

    @SerializedName("low_24h")
    val low24h: Double?
)

// MODELOS PARA TRENDING (Cryptos en tendencia)

/**
 * Respuesta del endpoint /search/trending
 */
data class TrendingResponse(
    @SerializedName("coins")
    val coins: List<TrendingCoinItem>
)

data class TrendingCoinItem(
    @SerializedName("item")
    val item: TrendingCoin
)

data class TrendingCoin(
    @SerializedName("id")
    val id: String,

    @SerializedName("coin_id")
    val coinId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int?,

    @SerializedName("thumb")
    val thumb: String,

    @SerializedName("small")
    val small: String,

    @SerializedName("large")
    val large: String,

    @SerializedName("score")
    val score: Int,

    @SerializedName("data")
    val data: TrendingCoinData?
)

data class TrendingCoinData(
    @SerializedName("price")
    val price: Double?,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Map<String, Double>?
)


// MODELOS PARA DETALLE DE CRYPTO


/**
 * Respuesta del endpoint /coins/{id}
 */
data class CryptoDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("symbol")
    val symbol: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: Description?,

    @SerializedName("image")
    val image: CryptoImage,

    @SerializedName("market_cap_rank")
    val marketCapRank: Int?,

    @SerializedName("market_data")
    val marketData: MarketData?,

    @SerializedName("categories")
    val categories: List<String>?,

    @SerializedName("links")
    val links: CryptoLinks?
)

data class Description(
    @SerializedName("en")
    val en: String?
)

data class CryptoImage(
    @SerializedName("thumb")
    val thumb: String,

    @SerializedName("small")
    val small: String,

    @SerializedName("large")
    val large: String
)

data class MarketData(
    @SerializedName("current_price")
    val currentPrice: Map<String, Double>?,

    @SerializedName("market_cap")
    val marketCap: Map<String, Long>?,

    @SerializedName("total_volume")
    val totalVolume: Map<String, Long>?,

    @SerializedName("high_24h")
    val high24h: Map<String, Double>?,

    @SerializedName("low_24h")
    val low24h: Map<String, Double>?,

    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,

    @SerializedName("price_change_percentage_7d")
    val priceChangePercentage7d: Double?,

    @SerializedName("price_change_percentage_30d")
    val priceChangePercentage30d: Double?,

    @SerializedName("circulating_supply")
    val circulatingSupply: Double?,

    @SerializedName("total_supply")
    val totalSupply: Double?,

    @SerializedName("max_supply")
    val maxSupply: Double?,

    @SerializedName("ath")
    val ath: Map<String, Double>?,

    @SerializedName("ath_date")
    val athDate: Map<String, String>?,

    @SerializedName("atl")
    val atl: Map<String, Double>?,

    @SerializedName("atl_date")
    val atlDate: Map<String, String>?
)

data class CryptoLinks(
    @SerializedName("homepage")
    val homepage: List<String>?,

    @SerializedName("blockchain_site")
    val blockchainSite: List<String>?,

    @SerializedName("subreddit_url")
    val subredditUrl: String?,

    @SerializedName("twitter_screen_name")
    val twitterScreenName: String?
)

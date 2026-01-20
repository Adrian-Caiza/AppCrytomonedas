package com.epn.criptoapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.epn.criptoapi.data.model.CryptoDetail
import com.epn.criptoapi.ui.viewmodel.CryptoDetailUiState
import com.epn.criptoapi.ui.viewmodel.CryptoDetailViewModel
import java.text.NumberFormat
import java.util.*

/**
 * 📈 Pantalla de Detalle de Criptomoneda
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    viewModel: CryptoDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar datos cuando cambia el ID
    LaunchedEffect(cryptoId) {
        viewModel.loadCryptoDetail(cryptoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is CryptoDetailUiState.Success -> state.crypto.name
                            else -> "Detalle"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadCryptoDetail(cryptoId) }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refrescar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is CryptoDetailUiState.Loading -> {
                    DetailLoadingContent()
                }
                is CryptoDetailUiState.Success -> {
                    DetailContent(crypto = state.crypto)
                }
                is CryptoDetailUiState.Error -> {
                    DetailErrorContent(
                        message = state.message,
                        onRetry = { viewModel.loadCryptoDetail(cryptoId) }
                    )
                }
            }
        }
    }
}

@Composable
fun DetailContent(crypto: CryptoDetail) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header con imagen y precio
        CryptoHeader(crypto)

        Spacer(modifier = Modifier.height(24.dp))

        // Estadísticas de precio
        PriceStatsCard(crypto)

        Spacer(modifier = Modifier.height(16.dp))

        // Cambios de precio
        PriceChangeCard(crypto)

        Spacer(modifier = Modifier.height(16.dp))

        // Supply Info
        SupplyInfoCard(crypto)

        Spacer(modifier = Modifier.height(16.dp))

        // ATH y ATL
        AthAtlCard(crypto)

        // Descripción
        crypto.description?.en?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(modifier = Modifier.height(16.dp))
            DescriptionCard(description)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CryptoHeader(crypto: CryptoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen grande
            AsyncImage(
                model = crypto.image.large,
                contentDescription = crypto.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre y símbolo
            Text(
                text = crypto.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = crypto.symbol.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            crypto.marketCapRank?.let { rank ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "Rank #$rank",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Precio actual
            crypto.marketData?.currentPrice?.get("usd")?.let { price ->
                Text(
                    text = formatCurrency(price),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Cambio 24h
            crypto.marketData?.priceChangePercentage24h?.let { change ->
                val changeColor = if (change >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                val changePrefix = if (change >= 0) "+" else ""
                Text(
                    text = "$changePrefix${String.format("%.2f", change)}% (24h)",
                    style = MaterialTheme.typography.titleMedium,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PriceStatsCard(crypto: CryptoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Estadísticas de Mercado",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Máximo 24h",
                    value = crypto.marketData?.high24h?.get("usd")?.let { formatCurrency(it) } ?: "-",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "Mínimo 24h",
                    value = crypto.marketData?.low24h?.get("usd")?.let { formatCurrency(it) } ?: "-",
                    color = Color(0xFFF44336)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Market Cap",
                    value = crypto.marketData?.marketCap?.get("usd")?.let { formatLargeNumber(it) } ?: "-"
                )
                StatItem(
                    label = "Volumen 24h",
                    value = crypto.marketData?.totalVolume?.get("usd")?.let { formatLargeNumber(it) } ?: "-"
                )
            }
        }
    }
}

@Composable
fun PriceChangeCard(crypto: CryptoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📈 Cambios de Precio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PriceChangeItem(
                    period = "24h",
                    change = crypto.marketData?.priceChangePercentage24h
                )
                PriceChangeItem(
                    period = "7d",
                    change = crypto.marketData?.priceChangePercentage7d
                )
                PriceChangeItem(
                    period = "30d",
                    change = crypto.marketData?.priceChangePercentage30d
                )
            }
        }
    }
}

@Composable
fun PriceChangeItem(period: String, change: Double?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = period,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        change?.let {
            val color = if (it >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            val prefix = if (it >= 0) "+" else ""
            Text(
                text = "$prefix${String.format("%.2f", it)}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        } ?: Text(
            text = "-",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun SupplyInfoCard(crypto: CryptoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💰 Supply",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            crypto.marketData?.circulatingSupply?.let {
                SupplyRow("Circulante", formatSupply(it))
            }
            crypto.marketData?.totalSupply?.let {
                SupplyRow("Total", formatSupply(it))
            }
            crypto.marketData?.maxSupply?.let {
                SupplyRow("Máximo", formatSupply(it))
            }
        }
    }
}

@Composable
fun SupplyRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AthAtlCard(crypto: CryptoDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏆 Récords Históricos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ATH (Máximo)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = crypto.marketData?.ath?.get("usd")?.let { formatCurrency(it) } ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ATL (Mínimo)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFF44336)
                    )
                    Text(
                        text = crypto.marketData?.atl?.get("usd")?.let { formatCurrency(it) } ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DescriptionCard(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📝 Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Limpiar HTML básico de la descripción
            val cleanDescription = description
                .replace(Regex("<[^>]*>"), "")
                .replace("&nbsp;", " ")
                .take(500) + if (description.length > 500) "..." else ""

            Text(
                text = cleanDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun DetailLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando detalle...")
        }
    }
}

@Composable
fun DetailErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "❌", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error al cargar detalle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

// Funciones de formateo
fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(value)
}

fun formatLargeNumber(value: Long): String {
    return when {
        value >= 1_000_000_000_000 -> String.format("$%.2fT", value / 1_000_000_000_000.0)
        value >= 1_000_000_000 -> String.format("$%.2fB", value / 1_000_000_000.0)
        value >= 1_000_000 -> String.format("$%.2fM", value / 1_000_000.0)
        else -> NumberFormat.getCurrencyInstance(Locale.US).format(value)
    }
}

fun formatSupply(value: Double): String {
    return when {
        value >= 1_000_000_000 -> String.format("%.2fB", value / 1_000_000_000.0)
        value >= 1_000_000 -> String.format("%.2fM", value / 1_000_000.0)
        value >= 1_000 -> String.format("%.2fK", value / 1_000.0)
        else -> String.format("%.0f", value)
    }
}

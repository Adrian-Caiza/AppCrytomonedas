package com.epn.criptoapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.epn.criptoapi.ui.navigation.MainScreen
import com.epn.criptoapi.ui.theme.CriptoApiTheme

/**
 * MainActivity - Punto de entrada de la aplicación
 *
 * Esta app consume la API de CoinGecko para mostrar
 * información de criptomonedas en tiempo real.
 *
 * Funcionalidades:
 * -  Lista de cryptos por capitalización de mercado
 * -  Trending: cryptos más buscadas
 * -  Detalle completo de cada crypto
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CriptoApiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pantalla principal con navegación por tabs
                    MainScreen()
                }
            }
        }
    }
}

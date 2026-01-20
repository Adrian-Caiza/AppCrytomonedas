package com.epn.criptoapi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.epn.criptoapi.data.model.CryptoDetail
import com.epn.criptoapi.data.repository.CryptoRepository
import com.epn.criptoapi.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados de la UI para el Detalle
 */
sealed class CryptoDetailUiState {
    object Loading : CryptoDetailUiState()
    data class Success(val crypto: CryptoDetail) : CryptoDetailUiState()
    data class Error(val message: String) : CryptoDetailUiState()
}

/**
 * ViewModel para la pantalla de Detalle de Crypto
 * Hereda de AndroidViewModel para tener acceso al Contexto (necesario para SharedPreferences)
 */
class CryptoDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CryptoRepository()
    // Inicializamos el repositorio de favoritos con el contexto de la aplicación
    private val favoritesRepository = FavoritesRepository(application)

    private val _uiState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()

    // Estado para saber si la moneda actual es favorita
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    /**
     * Carga el detalle de la criptomoneda y verifica si es favorita
     */
    fun loadCryptoDetail(cryptoId: String) {
        viewModelScope.launch {
            // 1. Verificamos si es favorita (localmente, es rápido)
            _isFavorite.value = favoritesRepository.isFavorite(cryptoId)

            // 2. Cargamos los datos de la API
            _uiState.value = CryptoDetailUiState.Loading

            repository.getCryptoDetail(cryptoId)
                .onSuccess { crypto ->
                    _uiState.value = CryptoDetailUiState.Success(crypto)
                }
                .onFailure { error ->
                    _uiState.value = CryptoDetailUiState.Error(
                        error.message ?: "Error desconocido"
                    )
                }
        }
    }

    /**
     * Alterna el estado de favorito (Guardar/Eliminar)
     */
    fun toggleFavorite(cryptoId: String) {
        favoritesRepository.toggleFavorite(cryptoId)
        // Actualizamos el estado observable inmediatamente
        _isFavorite.value = favoritesRepository.isFavorite(cryptoId)
    }
}
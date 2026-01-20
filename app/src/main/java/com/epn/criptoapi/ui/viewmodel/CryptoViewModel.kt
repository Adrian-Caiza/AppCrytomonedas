package com.epn.criptoapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.criptoapi.data.model.Crypto
import com.epn.criptoapi.data.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles de la UI
 */
sealed class CryptoUiState {
    object Loading : CryptoUiState()
    data class Success(val cryptos: List<Crypto>) : CryptoUiState()
    data class Error(val message: String) : CryptoUiState()
}

/**
 * ViewModel que maneja la lógica de negocio y estado de la UI
 *
 * ¿Por qué ViewModel?
 * - Sobrevive a cambios de configuración (rotación de pantalla)
 * - Separa la lógica de negocio de la UI
 * - Maneja el ciclo de vida correctamente
 */
class CryptoViewModel : ViewModel() {

    private val repository = CryptoRepository()

    // Estado de la UI usando StateFlow
    private val _uiState = MutableStateFlow<CryptoUiState>(CryptoUiState.Loading)
    val uiState: StateFlow<CryptoUiState> = _uiState.asStateFlow()

    // Lista completa para búsqueda local
    private var allCryptos: List<Crypto> = emptyList()

    // Query de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Cargar datos al iniciar
        loadCryptos()
    }

    /**
     * Carga la lista de criptomonedas desde la API
     */
    fun loadCryptos() {
        viewModelScope.launch {
            _uiState.value = CryptoUiState.Loading

            repository.getCryptoList()
                .onSuccess { cryptos ->
                    allCryptos = cryptos
                    _uiState.value = CryptoUiState.Success(cryptos)
                }
                .onFailure { error ->
                    _uiState.value = CryptoUiState.Error(
                        error.message ?: "Error desconocido al cargar datos"
                    )
                }
        }
    }

    /**
     * Filtra las criptomonedas por nombre o símbolo
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            _uiState.value = CryptoUiState.Success(allCryptos)
        } else {
            val filtered = allCryptos.filter { crypto ->
                crypto.name.contains(query, ignoreCase = true) ||
                        crypto.symbol.contains(query, ignoreCase = true)
            }
            _uiState.value = CryptoUiState.Success(filtered)
        }
    }

    /**
     * Refresca los datos desde la API
     */
    fun refresh() {
        _searchQuery.value = ""
        loadCryptos()
    }
}

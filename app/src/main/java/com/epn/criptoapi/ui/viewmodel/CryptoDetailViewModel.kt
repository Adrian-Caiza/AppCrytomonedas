package com.epn.criptoapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.criptoapi.data.model.CryptoDetail
import com.epn.criptoapi.data.repository.CryptoRepository
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
 */
class CryptoDetailViewModel : ViewModel() {

    private val repository = CryptoRepository()

    private val _uiState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)
    val uiState: StateFlow<CryptoDetailUiState> = _uiState.asStateFlow()

    fun loadCryptoDetail(cryptoId: String) {
        viewModelScope.launch {
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
}

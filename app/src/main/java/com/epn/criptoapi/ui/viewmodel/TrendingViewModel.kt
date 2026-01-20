package com.epn.criptoapi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.criptoapi.data.model.TrendingCoin
import com.epn.criptoapi.data.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados de la UI para Trending
 */
sealed class TrendingUiState {
    object Loading : TrendingUiState()
    data class Success(val coins: List<TrendingCoin>) : TrendingUiState()
    data class Error(val message: String) : TrendingUiState()
}

/**
 * ViewModel para la pantalla de Trending
 */
class TrendingViewModel : ViewModel() {

    private val repository = CryptoRepository()

    private val _uiState = MutableStateFlow<TrendingUiState>(TrendingUiState.Loading)
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = TrendingUiState.Loading

            repository.getTrendingCryptos()
                .onSuccess { coins ->
                    _uiState.value = TrendingUiState.Success(coins)
                }
                .onFailure { error ->
                    _uiState.value = TrendingUiState.Error(
                        error.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun refresh() {
        loadTrending()
    }
}

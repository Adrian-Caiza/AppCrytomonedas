package com.epn.criptoapi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.epn.criptoapi.data.model.Crypto
import com.epn.criptoapi.data.repository.CryptoRepository
import com.epn.criptoapi.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val favorites: List<Crypto>) : FavoritesUiState()
    object Empty : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesRepository = FavoritesRepository(application)
    private val cryptoRepository = CryptoRepository()

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoritesRepository.favoriteIds.collectLatest { ids ->
                if (ids.isEmpty()) {
                    _uiState.value = FavoritesUiState.Empty
                } else {
                    loadFavoriteDetails(ids)
                }
            }
        }
    }

    private fun loadFavoriteDetails(ids: Set<String>) {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            // Unimos los IDs con comas para la API de CoinGecko (ej: "bitcoin,ethereum")
            val idsString = ids.joinToString(",")

            cryptoRepository.searchCrypto(idsString)
                .onSuccess { cryptos ->
                    if (cryptos.isEmpty()) {
                        _uiState.value = FavoritesUiState.Empty
                    } else {
                        _uiState.value = FavoritesUiState.Success(cryptos)
                    }
                }
                .onFailure {
                    _uiState.value = FavoritesUiState.Error("Error al cargar favoritos")
                }
        }
    }

    // Método para refrescar manualmente
    fun refresh() {
        observeFavorites()
    }
}
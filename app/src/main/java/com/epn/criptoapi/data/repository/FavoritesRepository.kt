package com.epn.criptoapi.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("crypto_favorites", Context.MODE_PRIVATE)
    private val _favoriteIds = MutableStateFlow<Set<String>>(getFavoritesFromPrefs())

    // Exponemos un Flow para que la UI se actualice automáticamente
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private fun getFavoritesFromPrefs(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    fun toggleFavorite(id: String) {
        val currentFavorites = _favoriteIds.value.toMutableSet()
        if (currentFavorites.contains(id)) {
            currentFavorites.remove(id)
        } else {
            currentFavorites.add(id)
        }

        // Guardar en SharedPreferences
        prefs.edit().putStringSet("favorites", currentFavorites).apply()

        // Actualizar el estado en memoria
        _favoriteIds.value = currentFavorites
    }

    fun isFavorite(id: String): Boolean {
        return _favoriteIds.value.contains(id)
    }
}
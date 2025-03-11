package com.example.cheapchomp.ui.state

import com.example.cheapchomp.network.models.ProductPrice

sealed interface KrogerProductUiState {
    data object Initial : KrogerProductUiState
    data object Loading : KrogerProductUiState
    data class Success(val products: List<ProductPrice>) : KrogerProductUiState
    data class Error(val message: String) : KrogerProductUiState
}
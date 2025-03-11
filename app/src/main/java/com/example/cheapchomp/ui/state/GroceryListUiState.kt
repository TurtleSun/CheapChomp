package com.example.cheapchomp.ui.state

import com.example.cheapchomp.repository.DatabaseRepository.GroceryItem

sealed interface GroceryListUiState {
    data object Loading : GroceryListUiState
    data class Success(val groceryItems: List<GroceryItem>) : GroceryListUiState
    data class Error(val message: String) : GroceryListUiState
    data object Empty : GroceryListUiState
}
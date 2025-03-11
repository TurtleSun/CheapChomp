package com.example.cheapchomp.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.cheapchomp.repository.KrogerRepository
import com.example.cheapchomp.repository.DatabaseRepository
import com.example.cheapchomp.network.models.ProductPrice
import com.example.cheapchomp.repository.OfflineDatabase
import com.example.cheapchomp.ui.state.KrogerProductUiState
import com.google.firebase.auth.FirebaseAuth

class KrogerProductViewModel(
    private val krogerRepository: KrogerRepository,
    private val databaseRepository: DatabaseRepository,
    private val room_db: OfflineDatabase,
    private val auth:FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow<KrogerProductUiState>(KrogerProductUiState.Initial)
    val uiState: StateFlow<KrogerProductUiState> = _uiState.asStateFlow()

    private val _nearestStoreId = MutableStateFlow("")
    val nearestStoreId: StateFlow<String> = _nearestStoreId.asStateFlow()

    private var accessToken: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchProducts(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = KrogerProductUiState.Loading

            try {
                // Get or refresh access token if needed
                if (accessToken == null) {
                    krogerRepository.getAccessToken()
                        .onSuccess { token -> accessToken = token }
                        .onFailure { throw it }
                }

                // Use the token and store ID to search products
                accessToken?.let { token ->
                    if (_nearestStoreId.value.isNotEmpty()) {
                        krogerRepository.searchProducts(token, _nearestStoreId.value, query)
                            .onSuccess { products ->
                                _uiState.value = if (products.isEmpty()) {
                                    KrogerProductUiState.Error("No products found")
                                } else {
                                    KrogerProductUiState.Success(products)
                                }
                            }
                            .onFailure { throw it }
                    } else {
                        _uiState.value = KrogerProductUiState.Error("Store not initialized")
                    }
                } ?: throw Exception("Access token not available")
            } catch (e: Exception) {
                _uiState.value = KrogerProductUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeStore(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                krogerRepository.getAccessToken()
                    .onSuccess { token ->
                        accessToken = token
                        krogerRepository.findNearestStore(token, latitude, longitude)
                            .onSuccess { storeId ->
                                _nearestStoreId.value = storeId
                            }
                            .onFailure { e ->
                                _nearestStoreId.value = "70400357" // Fallback store ID
                                _uiState.value = KrogerProductUiState.Error(
                                    "Using fallback store: ${e.message}"
                                )
                            }
                    }
                    .onFailure { e ->
                        _uiState.value = KrogerProductUiState.Error(
                            "Failed to get access token: ${e.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = KrogerProductUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Add methods for database operations
    @RequiresApi(Build.VERSION_CODES.O)
    fun addToDatabase(product: ProductPrice, quantity: Int) {
        databaseRepository.addToDatabase(
            product = product,
            storeId = _nearestStoreId.value,
            quantity = quantity,
            room_db = room_db
        )
    }

    fun deleteFromDatabase(product: ProductPrice, storeId: String, onSuccess: () -> Unit) {
        databaseRepository.deleteFromDatabase(
            product = product,
            storeId = storeId,
            onSuccess = onSuccess
        )
    }

    // room database methods
    fun displayCachedProducts() {
        viewModelScope.launch {
            databaseRepository.displayCachedProducts(room_db, "all")
                .onSuccess { products ->
                    if (products.isEmpty()) {
                        _uiState.value = KrogerProductUiState.Error("No cached products found")
                    } else {
                        _uiState.value = KrogerProductUiState.Success(products)
                    }
                }
                .onFailure { exception ->
                    // Handle the exception, e.g., log it or update the UI state with an error
                    Log.e("displayCachedProducts", "Error fetching cached products", exception)
                    _uiState.value = KrogerProductUiState.Error("Error fetching cached products")
                }
        }
    }
    //Function to clear local cached products from the offline database
    fun clearCachedProducts() {
        databaseRepository.clearCachedProducts(room_db)
        _uiState.value = KrogerProductUiState.Initial
    }
    //Function to display favorited products in offline database
    fun displayFavoriteProducts() {
        viewModelScope.launch {
            databaseRepository.displayCachedProducts(room_db, "favorites")
                .onSuccess { products ->
                    if (products.isEmpty()) {
                        _uiState.value = KrogerProductUiState.Error("No favorited products found")
                    } else {
                        _uiState.value = KrogerProductUiState.Success(products)
                    }
                }
                .onFailure { exception ->
                    // Handle the exception, e.g., log it or update the UI state with an error
                    Log.e("displayFavoriteProducts", "Error fetching favorited products", exception)
                    _uiState.value = KrogerProductUiState.Error("Error fetching favorited products")
                }
        }
    }
    //Function to check if product is in favorites in offline database
    suspend fun isFavoriteProduct(productName: String): Boolean {
        val favoriteProducts = databaseRepository.displayCachedProducts(room_db, "favorites").getOrDefault(emptyList())
        return favoriteProducts.any { it.name == productName }
    }
    //Function to Add Product to favorites in offline database
    fun addToFavorites(product: ProductPrice) {
        databaseRepository.addToFavorites(product, _nearestStoreId.value, room_db)
        Log.d("DATABASE", "Added to favorites: $product")
        databaseRepository.getUserRef { userRef ->
            Log.d("DATABASE", "Favorites: ${room_db.itemsDao().getFavoriteItems(userRef.id)}")
        }
    }
    //Function to remove from favorites list in offline database
    fun removeFromFavorites(product: ProductPrice) {
        databaseRepository.removeFromFavorites(product, room_db)
        Log.d("DATABASE", "Removed from favorites: $product")
        databaseRepository.getUserRef { userRef ->
            Log.d("DATABASE", "Favorites: ${room_db.itemsDao().getFavoriteItems(userRef.id)}")
        }
    }
    //Sign Out function with firebase
    fun signOut() {
        auth.signOut()
        Log.d("Firebase", "Signed out successfully")
    }
}
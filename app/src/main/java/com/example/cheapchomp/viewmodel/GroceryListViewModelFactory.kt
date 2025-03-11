package com.example.cheapchomp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cheapchomp.repository.OfflineDatabase
import com.google.firebase.auth.FirebaseAuth

class GroceryListViewModelFactory(
    private val auth: FirebaseAuth,
    private val room_db: OfflineDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroceryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroceryListViewModel(auth = auth, room_db = room_db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
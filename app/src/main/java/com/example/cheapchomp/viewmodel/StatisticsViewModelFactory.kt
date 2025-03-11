package com.example.cheapchomp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cheapchomp.repository.DatabaseRepository
import com.example.cheapchomp.repository.OfflineDatabase
import com.google.firebase.auth.FirebaseAuth

class StatisticsViewModelFactory(
    private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(
                databaseRepository = DatabaseRepository(),
                auth = auth
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
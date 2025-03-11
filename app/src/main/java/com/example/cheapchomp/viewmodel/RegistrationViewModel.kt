package com.example.cheapchomp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.cheapchomp.repository.DatabaseRepository
import com.example.cheapchomp.ui.state.RegistrationUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// RegistrationViewModel.kt
class RegistrationViewModel(
    private val auth: FirebaseAuth,  // This needs to be FirebaseAuth, not just Auth
    private val db: FirebaseFirestore = Firebase.firestore,
    private val databaseRepository: DatabaseRepository = DatabaseRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Initial)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()
    //Registration function with Firebase
    fun register(email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _uiState.value = RegistrationUiState.Error("Passwords do not match!")
            return
        }

        _uiState.value = RegistrationUiState.Loading

        auth.createUserWithEmailAndPassword(email, password)  // Now this should work
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    createUserDocument(email)
                } else {
                    _uiState.value = RegistrationUiState.Error("Error creating account: ${task.exception?.message}")
                }
            }
    }

    // for initializing user & grocery list documents of a new google user specifically
    fun initializeDatabase() {
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("RegistrationViewModel", "User: $user")
        if (user != null) {
            val email = user.email

            db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // user not found, create a new user document
                        if (email != null) {
                            createUserDocument(email)
                        }


                    }
                }

        }
    }


    private fun createUserDocument(email: String) {
        val user = hashMapOf("email" to email)

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                createGroceryList()
                createExpenses()
            }
            .addOnFailureListener { e ->
                _uiState.value = RegistrationUiState.Error("Error creating user document: ${e.message}")
            }
    }

    private fun createGroceryList() {
        databaseRepository.getUserRef { userRef ->
            val groceryList = hashMapOf(
                "favorited" to false,
                "user" to userRef
            )

            db.collection("grocery_list")
                .add(groceryList)
                .addOnSuccessListener {
                    _uiState.value = RegistrationUiState.Success("Account created successfully!")
                }
                .addOnFailureListener { e ->
                    _uiState.value = RegistrationUiState.Error("Error creating grocery list: ${e.message}")
                }
        }
    }

    private fun createExpenses() {
        databaseRepository.getUserRef { userRef ->
            val expenses = hashMapOf(
                "user" to userRef,
                "year" to databaseRepository.getCurrentYear(),
                "1" to 0,
                "2" to 0,
                "3" to 0,
                "4" to 0,
                "5" to 0,
                "6" to 0,
                "7" to 0,
                "8" to 0,
                "9" to 0,
                "10" to 0,
                "11" to 0,
                "12" to 0
            )

            db.collection("expenses")
                .add(expenses)
                .addOnSuccessListener {
                    _uiState.value = RegistrationUiState.Success("Account created successfully!")
                }
                .addOnFailureListener { e ->
                    _uiState.value = RegistrationUiState.Error("Error creating expenses: ${e.message}")
                }
        }
    }
}
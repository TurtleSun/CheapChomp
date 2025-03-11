package com.example.cheapchomp.viewmodel


import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.cheapchomp.repository.DatabaseRepository
import com.example.cheapchomp.ui.state.LoginUiState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    private val auth: FirebaseAuth,
) : ViewModel() {
    //State Flow for login
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    //Normal Login Function with Firebase
    fun login(email: String, password: String) {
        _uiState.value = LoginUiState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = LoginUiState.Success("Login successful :)")
                    _isLoggedIn.value = true
                } else {
                    _uiState.value = LoginUiState.Error("Login failed: ${task.exception?.message}")
                }
            }
    }


    // Google OAuth Login with Firebase
    fun startGoogleSignIn(
        oneTapClient: SignInClient,
        signInRequest: BeginSignInRequest,
        onSuccess: (IntentSender) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                onSuccess(result.pendingIntent.intentSender)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    // Handle Google Sign-In Result and makes sure a valid ID token is retrieved
    fun handleSignInResult(oneTapClient: SignInClient, data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken)
            } else {
                _uiState.value = LoginUiState.Error("No ID token found")
            }
        } catch (e: ApiException) {
            _uiState.value = LoginUiState.Error("Error retrieving sign-in credential")
        }
    }
    //Checks if the Google credential sign in was successful or not
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.value = LoginUiState.Success("Login successful :)")
                    setLoggedIn(true)
                } else {
                    _uiState.value = LoginUiState.Error("Firebase sign-in failed")
                    setLoggedIn(false)
                }
            }
    }
    //Triggers the swap to KrogerProductScreen
    fun setLoggedIn(value: Boolean) {
        _isLoggedIn.value = value
    }


}

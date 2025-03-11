package com.example.cheapchomp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cheapchomp.R
import com.example.cheapchomp.ui.state.RegistrationUiState
import com.example.cheapchomp.viewmodel.RegistrationViewModel
import com.example.cheapchomp.viewmodel.RegistrationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
//Registration Screen
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    auth: FirebaseAuth
) {
    //Variables
    val viewModel: RegistrationViewModel = viewModel(
        factory = RegistrationViewModelFactory(auth)
    )
    val uiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val intSizeSaver = Saver<IntSize, Pair<Int,Int>>(save = {it.width to it.height}, restore = {IntSize(it.first, it.second)})
    var textFieldSize2 by rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize.Zero) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Navigate on success
    LaunchedEffect(uiState) {
        if (uiState is RegistrationUiState.Success) {
            navController.navigate("LoginScreen")
        }
    }
    //Landscape Mode
    if(isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .weight(1f)
            ) {
                //Input Fields
                Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .width(400.dp)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize2 = coordinates.size
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(400.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(400.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                //Register account in Firebase
                Button(
                    onClick = { viewModel.register(email, password, confirmPassword) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                    modifier = Modifier.width(with(LocalDensity.current) { textFieldSize2.width.toDp() })
                ) {
                    when (uiState) {
                        is RegistrationUiState.Loading -> CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        else -> Text("Create Account")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .weight(.75f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.weight(1f)
                )
                //Redirect back to Login Screen after registration
                Text("Already have an account?")
                Text(
                    text = "Login",
                    modifier = Modifier.clickable(onClick = { navController.navigate("LoginScreen") }),
                    color = Color(0xFF006FAD)
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Display message based on UI state
                val displayMessage = when (uiState) {
                    is RegistrationUiState.Success -> (uiState as RegistrationUiState.Success).message
                    is RegistrationUiState.Error -> (uiState as RegistrationUiState.Error).message
                    else -> ""
                }
                Text(displayMessage, modifier = Modifier.widthIn(max = 250.dp))
            }
        }
    }
    //Portrait Mode
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //Inputs
            Text("Sign Up", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    textFieldSize2 = coordinates.size
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            //Registration with Firebase
            Button(
                onClick = { viewModel.register(email, password, confirmPassword) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize2.width.toDp() })
            ) {
                when (uiState) {
                    is RegistrationUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    else -> Text("Create Account")
                }
            }

            Button(
                onClick = { navController.navigate("LoginScreen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                modifier = Modifier.width(with(LocalDensity.current) { textFieldSize2.width.toDp() })
            ) {
                Text("Back to Login")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display message based on UI state
            val displayMessage = when (uiState) {
                is RegistrationUiState.Success -> (uiState as RegistrationUiState.Success).message
                is RegistrationUiState.Error -> (uiState as RegistrationUiState.Error).message
                else -> ""
            }
            Text(displayMessage, modifier = Modifier.widthIn(max = 250.dp))
        }
    }
}
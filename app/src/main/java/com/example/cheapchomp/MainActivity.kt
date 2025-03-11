package com.example.cheapchomp

import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import com.example.cheapchomp.repository.NetworkConnectivityListener
import com.example.cheapchomp.repository.OfflineDatabase
import com.example.cheapchomp.ui.screens.GoogleMapScreen
import com.example.cheapchomp.ui.screens.GroceryListScreen
import com.example.cheapchomp.ui.screens.KrogerProductScreen
import com.example.cheapchomp.ui.screens.LoginScreen
import com.example.cheapchomp.ui.screens.RegistrationScreen
import com.example.cheapchomp.ui.screens.StatisticsScreen
import com.example.cheapchomp.ui.theme.CheapChompTheme
import com.example.cheapchomp.viewmodel.LoginViewModel
import com.example.cheapchomp.viewmodel.LoginViewModelFactory
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var viewModel: LoginViewModel
    private val networkConnectivityListener by lazy { NetworkConnectivityListener(this) }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(true)
            // Log the user ID for better crash reporting
            FirebaseAuth.getInstance().currentUser?.let { user ->
                setUserId(user.uid)
            }
        }

        networkConnectivityListener.register()

        // ok so idk where to put this but it is refusing to go anywhere else i try
        // google oauth initialization
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(FirebaseAuth.getInstance())
        ).get(LoginViewModel::class.java)

        lateinit var oneTapClient: SignInClient
        lateinit var signInRequest: BeginSignInRequest

        fun initializeSignInClient(context: Context) {
            oneTapClient = Identity.getSignInClient(context)

            signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
        }
        initializeSignInClient(this)

        val googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.handleSignInResult(oneTapClient, result.data)
                } else {
                    Log.e("GoogleSignIn", "Sign-in canceled or failed")
                }
            }

        setContent {
            CheapChompTheme {
                Surface(color = Color(0xFFA3C9F6)) {
                    //GoogleMapScreen()
                    mainScreen(applicationContext, onGoogleSignInLauncher = { intentSender ->
                        val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                        googleSignInLauncher.launch(intentSenderRequest)
                    }, signInRequest = signInRequest, oneTapClient = oneTapClient)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun mainScreen(applicationContext: Context, onGoogleSignInLauncher: (IntentSender) -> Unit, signInRequest: BeginSignInRequest, oneTapClient: SignInClient) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val roomDB: OfflineDatabase = (applicationContext as MyApp).roomDB


    NavHost(navController = navController, startDestination = "LoginScreen") {
        composable("LoginScreen") {
            LoginScreen(navController = navController, auth = auth, onGoogleSignInLauncher = onGoogleSignInLauncher, oneTapClient = oneTapClient, signInRequest = signInRequest)
        }
        composable("RegistrationScreen") {
            RegistrationScreen(navController = navController, auth = auth)
        }
        composable("GroceryListScreen") {
            GroceryListScreen(navController = navController, auth = auth, room_db = roomDB)
        }
        composable("GoogleMapScreen") { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 0.0
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 0.0
            GoogleMapScreen(navController = navController, latitude = latitude, longitude = longitude)
        }
        composable("StatisticsScreen") {
            StatisticsScreen(navController = navController, auth = auth)
        }
        composable(
            route = "KrogerProductScreen/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("latitude") {
                    type = NavType.StringType
                    defaultValue = "37.7749"  // San Francisco default
                },
                navArgument("longitude") {
                    type = NavType.StringType
                    defaultValue = "-122.4194"  // San Francisco default
                }
            )

        ) { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 37.7749
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: -122.4194
            KrogerProductScreen(navController = navController, latitude = latitude, longitude = longitude, roomDB = roomDB,auth=auth)
        }
    }
}

class MyApp : Application() {
    val roomDB: OfflineDatabase by lazy {
        Room.databaseBuilder(
            this,
            OfflineDatabase::class.java,
            "items_database"
        ).allowMainThreadQueries().fallbackToDestructiveMigration()
            .build()
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheapChompTheme {
    }
}
package com.example.cheapchomp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cheapchomp.network.models.ProductPrice
import com.example.cheapchomp.repository.DatabaseRepository
import com.example.cheapchomp.ui.state.KrogerProductUiState
import com.example.cheapchomp.viewmodel.KrogerProductViewModel
import com.example.cheapchomp.viewmodel.KrogerProductViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.ui.platform.LocalConfiguration
import com.example.cheapchomp.repository.OfflineDatabase
import com.google.firebase.auth.FirebaseAuth

//Composable for the Kroger Product Lookup Screen
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun KrogerProductScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    latitude: Double,
    longitude: Double,
    roomDB: OfflineDatabase,
    auth: FirebaseAuth
) {
    //Variables and View Model
    val viewModel: KrogerProductViewModel = viewModel(
        factory = KrogerProductViewModelFactory(roomDB,auth)
    )
    val uiState by viewModel.uiState.collectAsState()
    val nearestStoreId by viewModel.nearestStoreId.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Get current orientation
    val configuration = LocalConfiguration.current
    val screenOrientation = when(configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
        else -> "Portrait"
    }

    // Initialize store data when screen is first created
    LaunchedEffect(latitude, longitude) {
        viewModel.initializeStore(latitude, longitude)
    }

    when (screenOrientation) {
        //Landscape Mode
        "Landscape" -> {
            Row(modifier = Modifier.fillMaxSize()) {
                // Navigation Rail
                NavigationRail(containerColor = Color(0xFF006FAD)) {
                    NavigationRailItem(
                        colors = NavigationRailItemColors(selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.White,
                            disabledTextColor = Color.White,
                        selectedIndicatorColor = Color.White),
                        icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") },
                        label = { Text("Back") },
                        selected = false,
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f),
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Statistics") },
                        label = { Text("Stats") },
                        selected = true,
                        onClick = { navController.navigate("StatisticsScreen") },
                        modifier = Modifier.weight(1f),
                        colors = NavigationRailItemColors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.Black,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color(0xFF006FAD)
                        )
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Product Search") },
                        label = { Text("Search") },
                        selected = true,
                        onClick = { /* current screen */ },
                        modifier = Modifier.weight(1f),
                        colors = NavigationRailItemColors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.Black,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color(0xFF006FAD)
                        )
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Grocery List") },
                        label = { Text("Grocery List") },
                        selected = false,
                        onClick = { navController.navigate("GroceryListScreen") },
                        modifier = Modifier.weight(1f),
                        colors = NavigationRailItemColors(selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.White,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color.White)
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.AccountBox, contentDescription = "Sign Out") },
                        label = { Text("Sign Out") },
                        selected = true,
                        onClick = { viewModel.signOut(); navController.navigate("LoginScreen") },
                        modifier = Modifier.weight(1f),
                        colors = NavigationRailItemColors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color(0xFF006FAD),
                            disabledIconColor = Color.Black,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color(0xFF006FAD)
                        )
                    )
                }

                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Kroger Product Lookup", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Enter product name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                viewModel.searchProducts(searchQuery)
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.searchProducts(searchQuery) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                            enabled = uiState != KrogerProductUiState.Loading
                        ) {
                            if (uiState is KrogerProductUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Search")
                            }
                        }
                    }

                    // Results Section
                    when (uiState) {
                        is KrogerProductUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        is KrogerProductUiState.Success -> {
                            val products = (uiState as KrogerProductUiState.Success).products
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(products) { product ->
                                    KrogerProductItem(
                                        product = product,
                                        nearestStoreId = nearestStoreId,
                                        onAddToDatabase = { quantity ->
                                            viewModel.addToDatabase(product, quantity)
                                        },
                                        onAddToFavorites = { prod ->
                                            viewModel.addToFavorites(prod)
                                        },
                                        onRemoveFromFavorites = { prod ->
                                            viewModel.removeFromFavorites(prod)
                                        },
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                        is KrogerProductUiState.Error -> {
                            Text(
                                text = (uiState as KrogerProductUiState.Error).message,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> { /* Initial state */ }
                    }
                }
            }
        }
        else -> {
            // Portrait mode - Keep your existing layout
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Kroger Product Lookup", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search TextField
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Enter product name") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                viewModel.searchProducts(searchQuery)
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.searchProducts(searchQuery) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                            enabled = uiState != KrogerProductUiState.Loading
                        ) {
                            if (uiState is KrogerProductUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("Search")
                            }
                        }

                    }
                    // offline browsing/favorites
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.displayCachedProducts() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                            enabled = uiState != KrogerProductUiState.Loading
                        ) { Text("Browse Offline") }
                        Button(
                            onClick = { viewModel.clearCachedProducts() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                            enabled = uiState != KrogerProductUiState.Loading
                        ) { Text("Clear Cache") }
                        Button(
                            onClick = { viewModel.displayFavoriteProducts() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006FAD)),
                            enabled = uiState != KrogerProductUiState.Loading
                        ) { Text("Favorites") }
                    }

                    when (uiState) {
                        is KrogerProductUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        is KrogerProductUiState.Success -> {
                            val products = (uiState as KrogerProductUiState.Success).products
                            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = bottomPadding)
                            ) {
                                items(products) { product ->
                                    KrogerProductItem(
                                        product = product,
                                        nearestStoreId = nearestStoreId,
                                        onAddToDatabase = { quantity ->
                                            viewModel.addToDatabase(product, quantity)
                                        },
                                        onAddToFavorites = { prod ->
                                            viewModel.addToFavorites(prod)
                                        },
                                        onRemoveFromFavorites = { prod ->
                                            viewModel.removeFromFavorites(prod)
                                        },
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                        is KrogerProductUiState.Error -> {
                            Text(
                                text = (uiState as KrogerProductUiState.Error).message,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        else -> { /* Initial state */ }
                    }
                }

                // Bottom Navigation
                BottomNavigation(
                    backgroundColor = Color(0xFF006FAD),
                    elevation = 8.dp
                ) {
                    BottomNavigationItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) },
                        label = { Text("Back",color =Color.White)},
                        selected = false,
                        onClick = { navController.navigateUp() },
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Statistics",tint = Color.White) },
                        label = { Text("Stats",color =Color.White) },
                        selected = false,
                        onClick = { navController.navigate("StatisticsScreen") },
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Product Search",tint = Color.White) },
                        label = { Text("Search",color =Color.White) },
                        selected = true,
                        onClick = { /* current screen */ },
                    )

                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Grocery List",tint = Color.White) },
                        label = { Text("Grocery List",color =Color.White) },
                        selected = false,
                        onClick = { navController.navigate("GroceryListScreen") },
                    )
                    BottomNavigationItem(
                        icon = {Icon(Icons.Filled.AccountBox,contentDescription="Sign Out",tint = Color.White)},
                        label = {Text("Sign Out",color =Color.White)},
                        selected = false,
                        onClick = {
                            viewModel.signOut()
                            navController.navigate("LoginScreen")
                     }
                    )
                }
            }
        }
    }
}
//Uses this composable to generate the image from the retrieved image link from the Kroger API
@Composable
fun ProductImage(
    imageUrl: String?
){
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier.size(100.dp),
        contentScale = ContentScale.Crop
    )
}

//This Composable generates the individual product items after search querying the KrogerAPI
@Composable
fun KrogerProductItem(
    product: ProductPrice,
    nearestStoreId: String,
    onAddToDatabase: (quantity: Int) -> Unit,
    onAddToFavorites: (product: ProductPrice) -> Unit,
    onRemoveFromFavorites: (product: ProductPrice) -> Unit,
    viewModel: KrogerProductViewModel
) {
    var offset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val swipeThreshold = 300f
    var clickCount by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var manualQuantity by remember { mutableStateOf("1") }
    var existingQuantity by remember { mutableIntStateOf(0) }
    var showAddedMessage by remember { mutableStateOf(false) }
    var lastAddedQuantity by remember { mutableIntStateOf(0) }
    var favoriteIcon by remember { mutableStateOf(Icons.Default.FavoriteBorder) }
    var isFavorite by remember { mutableStateOf(false) }


    // Query Firestore to get initial quantity
    LaunchedEffect(product.name, nearestStoreId) {
        DatabaseRepository().getGroceryList { listRef ->
            val db = Firebase.firestore
            db.collection("items")
                .whereEqualTo("grocery_list", listRef)
                .whereEqualTo("store_id", nearestStoreId)
                .whereEqualTo("name", product.name)
                .get()
                .addOnSuccessListener { documents ->
                    existingQuantity = documents.sumOf { doc ->
                        doc.getLong("quantity")?.toInt() ?: 0
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error getting existing quantity", e)
                }
        }
    }

    LaunchedEffect(key1 = product.name) { // Launch effect when product.name changes
        isFavorite = viewModel.isFavoriteProduct(product.name)
        favoriteIcon = if (isFavorite) {
            Icons.Default.Favorite
        } else {
            Icons.Default.FavoriteBorder
        }
    }

    // Hide the message after delay
    LaunchedEffect(showAddedMessage) {
        if (showAddedMessage) {
            delay(2000) // Message shows for 2 seconds
            showAddedMessage = false
        }
    }

    val handleAddToDatabase = { quantity: Int ->
        existingQuantity += quantity
        lastAddedQuantity = quantity
        showAddedMessage = true
        onAddToDatabase(quantity)
    }

    val handleAddToFavorites = { prod: ProductPrice ->
        onAddToFavorites(prod)
    }
    val handleRemoveFromFavorites = { prod: ProductPrice ->
        onRemoveFromFavorites(prod)
    }

    // Animate the offset with a spring-like animation for smooth sling-back
    val animatedOffset by animateFloatAsState(
        targetValue = if (!isDragging) 0f else offset,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f),
        label = "offset animation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        // If swiped beyond threshold, add to database
                        if (offset >= swipeThreshold) {
                            handleAddToDatabase(1) // Swipe should be simplistic, only add 1
                        }
                        // Reset offset for next swipe
                        offset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        // Allow dragging regardless of previous swipes
                        offset = (offset + dragAmount).coerceIn(0f, swipeThreshold)
                    }
                )
            }
    ) {
        // Background (Add icon)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF56AE57).copy(alpha = 0.2f)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to List",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .scale(2f)
                    .alpha(animatedOffset / swipeThreshold),
                tint = Color(0xFF006FAD)
            )
        }

        // Foreground (Product Card)
        Card(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Product Image
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        ProductImage(imageUrl = product.imageUrl)
                    }

                    // Product Details Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Product Name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Price and Save to Grocery List in a row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$${product.price}",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF006FAD)
                            )

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        isFavorite = !isFavorite
                                        if (isFavorite) {
                                            handleAddToFavorites(product)
                                        } else {
                                            handleRemoveFromFavorites(product)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                favoriteIcon = if (isFavorite) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                }
                                Icon(
                                    imageVector = favoriteIcon,
                                    contentDescription = "Favorite",
                                    tint = Color(0xFF006FAD)
                                )
                            }

                            Button(
                                onClick = {
                                    clickCount++
                                    if (clickCount >= 3) {
                                        showDialog = true
                                    } else {
                                        handleAddToDatabase(1)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF006FAD)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Text("Save to List")
                            }
                        }
                        AnimatedVisibility(
                            visible = showAddedMessage,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = "Added $lastAddedQuantity item${if (lastAddedQuantity > 1) "s" else ""}",
                                color = Color(0xFF006FAD),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Quantity Badge
                if (existingQuantity > 0) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 12.dp, y = (-12).dp)
                            .background(
                                color = Color(0xFF006FAD),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = existingQuantity.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                clickCount = 0
            },
            title = { Text("Seems You Want Multiple Items...") },
            text = {
                Column {
                    Text("Enter the quantity you'd like to add:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = manualQuantity,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                manualQuantity = newValue
                            }
                        },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val quantity = manualQuantity.toIntOrNull() ?: 1
                        if (quantity > 0) {
                            handleAddToDatabase(quantity)
                            showDialog = false
                            clickCount = 0
                            manualQuantity = "1"
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        clickCount = 0
                        manualQuantity = "1"
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
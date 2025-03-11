package com.example.cheapchomp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cheapchomp.network.LocationService
import com.example.cheapchomp.viewmodel.StatisticsViewModel
import com.example.cheapchomp.viewmodel.StatisticsViewModelFactory
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.BarChartData.Bar
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

//This Composable is the Statistics Screen
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    auth: FirebaseAuth
    ) {
    //Variables
    val viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory(auth),
    )
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //Landscape Mode
    if (isLandscape) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Navigation Rail
                NavigationRail(containerColor = Color(0xFF006FAD)) {
                    NavigationRailItem(
                        colors = NavigationRailItemColors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.White,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color.White
                        ),
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        },
                        label = { Text("Back") },
                        selected = false,
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f),
                    )
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Statistics") },
                        label = { Text("Stats") },
                        selected = true,
                        onClick = { /*On this page*/ },
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
                    NavigationRailItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Product Search") },
                        label = { Text("Search") },
                        selected = true,
                        onClick = { val locationService = LocationService(context)
                            scope.launch {
                                try {
                                    val location = locationService.getCurrentLocation()
                                    navController.navigate(
                                        "KrogerProductScreen/${location.latitude}/${location.longitude}"
                                    )
                                } catch (e: Exception) {
                                    // If location fails, use default San Francisco coordinates
                                    navController.navigate("KrogerProductScreen/37.7749/-122.4194")
                                }
                            } },
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
                        icon = {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = "Grocery List"
                            )
                        },
                        label = { Text("Grocery List") },
                        selected = false,
                        onClick = { navController.navigate("GroceryListScreen") },
                        modifier = Modifier.weight(1f),
                        colors = NavigationRailItemColors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White,
                            disabledIconColor = Color.White,
                            disabledTextColor = Color.White,
                            selectedIndicatorColor = Color.White
                        )
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
                //Calls the Graph Composable which compares the monthly total grocery expenses of the user
                Card(shape = RoundedCornerShape(12.dp),colors = CardDefaults.cardColors(containerColor = Color.White),modifier=Modifier.padding(16.dp)) {
                    MyBarChartParent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        viewModel = viewModel
                    )
                }
            }
    }
        //Portrait Mode
        else {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            //Displays information about the page
            Spacer(modifier = Modifier.weight(.125f))
            Text("Statistics Screen", style = MaterialTheme.typography.headlineMedium)
            Text("Below is a graph of your monthly grocery expenses for the current year", modifier = Modifier.weight(.5f).padding(start=16.dp), style = MaterialTheme.typography.bodyLarge)
            //Calls the Graph Composable which compares the monthly total grocery expenses of the user
            Card(shape = RoundedCornerShape(12.dp),colors = CardDefaults.cardColors(containerColor = Color.White)) {
                MyBarChartParent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(16.dp),
                    viewModel = viewModel
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //Navigation
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
                    onClick = { /*Currently on screen*/},
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Product Search", tint = Color.White) },
                    label = { Text("Search", color = Color.White) },
                    selected = false,
                    onClick = {
                        val locationService = LocationService(context)
                        scope.launch {
                            try {
                                val location = locationService.getCurrentLocation()
                                navController.navigate(
                                    "KrogerProductScreen/${location.latitude}/${location.longitude}"
                                )
                            } catch (e: Exception) {
                                // If location fails, use default San Francisco coordinates
                                navController.navigate("KrogerProductScreen/37.7749/-122.4194")
                            }
                        }
                    }
                )

                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Grocery List",tint = Color.White) },
                    label = { Text("Grocery List",color =Color.White) },
                    selected = false,
                    onClick = { navController.navigate("GroceryListScreen")},
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

//Creates a Bar Chart using an imported Github library and our database information
@Composable
fun MyBarChartParent(modifier: Modifier, viewModel: StatisticsViewModel) {
    var expensesData by remember { mutableStateOf<List<Float>>(emptyList()) }
    val currentMonth = viewModel.getCurrentMonth()
    LaunchedEffect(Unit) {
        viewModel.getExpenses { expenses ->
            expensesData = expenses
        }
    }
    val bars = expensesData.mapIndexed { index, expense ->
        Bar(
            label = viewModel.getMonthLabel(currentMonth - index), // Function to get month label
            value = expense,
            color = Color(0xFF006FAD)
        )
    }
    BarChart(
        barChartData = BarChartData(bars = bars.reversed()),
        modifier = modifier,
        animation = simpleChartAnimation(),
        barDrawer = SimpleBarDrawer(),
        xAxisDrawer = SimpleXAxisDrawer(),
        yAxisDrawer = SimpleYAxisDrawer(),
        labelDrawer = SimpleValueDrawer()
    )

}

package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // NEW: Required for button layout in HomeContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults

// NAVIGATION IMPORTS
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType

// 1. Define the Student data class
data class Student(
    val name: String
)

// =================================================================
// 0. Reusable UI Components
// =================================================================

// UI Element for displaying a title
@Composable
fun OnBackgroundTitleText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground
    )
}

// UI Element for displaying an item list text
@Composable
fun OnBackgroundItemText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground
    )
}

// UI Element for displaying a primary button
@Composable
fun PrimaryTextButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(
            text = text,
            color = Color.White,
        )
    }
}

// =================================================================
// 4. MainActivity Class
// =================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                // 5. UPDATE: Replace Home() with App() and setup NavController
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    App(
                        navController = navController
                    )
                }
            }
        }
    }
}

// =================================================================
// NAVIGATION COMPONENTS
// =================================================================

// NEW: Root Composable (App)
// This will be the root composable of the app
@Composable
fun App(navController: NavHostController) {
    //Here, we use NavHost to create a navigation graph
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        //Here, we create a route called "home"
        composable("home") {
            // Pass the navigation lambda function to Home
            Home { listDataString ->
                navController.navigate("resultContent/?listData=$listDataString")
            }
        }
        //Here, we create a route called "resultContent"
        composable(
            "resultContent/?listData={listData}",
            arguments = listOf(navArgument("listData") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            // Pass the argument value to the ResultContent composable
            ResultContent(
                backStackEntry.arguments?.getString("listData").orEmpty()
            )
        }
    }
}

// 10. NEW: ResultContent Composable
// ResultContent accepts a String parameter called listData from the Home composable
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        // Added alignment for clarity
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Added a title for context
        OnBackgroundTitleText(text = "Result Content")
        // Display the received list data string
        OnBackgroundItemText(text = listData)
    }
}

// =================================================================
// STATE/LOGIC COMPONENTS
// =================================================================

// 2. State-managing Composable (Home - Parent component)
// 6. UPDATE: Add navigation parameter
@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit // Accepts a String to pass as argument
) {
    // List state for the student items
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}
    // State for the text field input
    var inputField = remember { mutableStateOf(Student("")) }

    // 8. UPDATE: Update calling function for HomeContent
    HomeContent(
        listData,
        inputField.value,
        onInputValueChange = { input ->
            // Fix: Use named parameter 'name' for copy function
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("") // Reset input field
            }
        },
        navigateFromHomeToResult = {
            // Convert list to string for navigation argument
            navigateFromHomeToResult(listData.toList().toString())
        }
    )
}

// =================================================================
// UI COMPONENTS
// =================================================================

// 3. UI Composable (HomeContent - Child component)
// 7. UPDATE: Add navigation parameter to HomeContent
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit // Lambda to trigger navigation
) {
    // 9. UPDATE: Update LazyColumn to include Row and a second button
    LazyColumn {
        // Input Form Item
        item {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundTitleText(text = stringResource(
                    id = R.string.enter_item)
                )

                TextField(
                    value = inputField.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = onInputValueChange
                )

                Row { // NEW: Row to hold the two buttons side-by-side
                    PrimaryTextButton(text = stringResource(id = R.string.button_click)) {
                        onButtonClick()
                    }
                    PrimaryTextButton(text = stringResource(id = R.string.button_navigate)) {
                        navigateFromHomeToResult()
                    }
                }
            }
        }

        // List of Student Items
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnBackgroundItemText(text = item.name)
            }
        }
    }
}

// Preview function
@Preview(showBackground = true)
@Composable
fun PreviewHomeContent() {
    LAB_WEEK_09Theme {
        HomeContent(
            listData = mutableStateListOf(Student("Tanu"), Student("Tina"), Student("Tono")),
            inputField = Student("Example"),
            onInputValueChange = {},
            onButtonClick = {},
            navigateFromHomeToResult = {}
        )
    }
}
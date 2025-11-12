package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // Required for button layout in HomeContent
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
                // Replace Home() with App() and setup NavController
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

// Root Composable (App)
@Composable
fun App(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            // Pass the navigation lambda function to Home
            Home { listDataString ->
                navController.navigate("resultContent/?listData=$listDataString")
            }
        }
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

// ResultContent Composable
@Composable
fun ResultContent(listData: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnBackgroundTitleText(text = "Result Content")
        OnBackgroundItemText(text = listData)
    }
}

// =================================================================
// STATE/LOGIC COMPONENTS
// =================================================================

// State-managing Composable (Home - Parent component)
@Composable
fun Home(
    navigateFromHomeToResult: (String) -> Unit
) {
    // List state for the student items
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}
    // State for the text field input
    var inputField = remember { mutableStateOf(Student("")) }

    HomeContent(
        listData,
        inputField.value,
        onInputValueChange = { input ->
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            // FIX: Prevent submission if the string is blank or empty
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

// UI Composable (HomeContent - Child component)
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>,
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    navigateFromHomeToResult: () -> Unit
) {
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

                Row { // Row to hold the two buttons side-by-side
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
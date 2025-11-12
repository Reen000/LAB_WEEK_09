package com.example.lab_week_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.snapshots.SnapshotStateList // Import needed for SnapshotStateList

// 1. Define the Student data class
// Declare a data class called Student
data class Student(
    val name: String // Changed var to val, though data classes are flexible. Keeping it simple.
)

//Previously we extend AppCompatActivity,
//now we extend ComponentActivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Here, we use setContent instead of setContentView
        setContent {
            //Here, we wrap our content with the theme
            //You can check out the LAB_WEEK_09Theme inside Theme.kt
            LAB_WEEK_09Theme {
                // A surface container using the 'background' color from the theme
                // UPDATED SURFACE BLOCK (Final Instruction)
                Surface(
                    //We use Modifier.fillMaxSize() to make the surface fill the whole
                    //screen
                    modifier = Modifier.fillMaxSize(),
                    //We use MaterialTheme.colorScheme.background to get the background
                    //color
                    //and set it as the color of the surface
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Call the state-managing Home composable without parameters
                    Home()
                }
            }
        }
    }
}

// 2. State-managing Composable (Home - Parent component)
@Composable
fun Home() {
    //Here, we create a mutable state list of Student
    //We use remember to make the list remember its value
    //This is so that the list won't be recreated when the composable
    //recomposes
    //We use mutableStateListOf to make the list mutable
    //This is so that we can add or remove items from the list
    val listData = remember { mutableStateListOf(
        Student("Tanu"),
        Student("Tina"),
        Student("Tono")
    )}
    //Here, we create a mutable state of Student
    //This is so that we can get the value of the input field
    var inputField = remember { mutableStateOf(Student("")) }

    //We call the HomeContent composable
    //Here, we pass:
    //listData to show the list of items inside HomeContent
    //inputField to show the input field value inside HomeContent
    //A lambda function to update the value of the inputField
    //A lambda function to add the inputField to the listData
    HomeContent(
        listData,
        inputField.value,
        onInputValueChange = { input ->
            // Fix: Use the named parameter 'name' for the data class copy
            inputField.value = inputField.value.copy(name = input)
        },
        onButtonClick = {
            if (inputField.value.name.isNotBlank()) {
                listData.add(inputField.value)
                inputField.value = Student("")
            }
        }
    )
}

// 3. UI Composable (HomeContent - Child component)
//Here, we create a composable function called HomeContent
//HomeContent is used to display the content of the Home composable
@Composable
fun HomeContent(
    listData: SnapshotStateList<Student>, // Added <Student> for type safety
    inputField: Student,
    onInputValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    //Here, we use LazyColumn to display a list of items lazily
    LazyColumn {
        //Here, we use item to display an item inside the LazyColumn
        item {
            Column(
                //Modifier.padding(16.dp) is used to add padding to the
                //Column
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                //Alignment.CenterHorizontally is used to align the Column
                //horizontally
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(
                    id = R.string.enter_item)
                )
                //Here, we use TextField to display a text input field
                TextField(
                    //Set the value of the input field
                    value = inputField.name,
                    //Set the keyboard type of the input field
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    //Set what happens when the value of the input field
                    //changes
                    onValueChange = {
                        //Here, we call the onInputValueChange lambda
                        //function
                        onInputValueChange(it)
                    }
                )
                //Here, we use Button to display a button
                //the onClick parameter is used to set what happens when the
                //button is clicked
                Button(onClick = {
                    //Here, we call the onButtonClick lambda function
                    onButtonClick()
                }) {
                    //Set the text of the button
                    Text(text = stringResource(
                        id = R.string.button_click)
                    )
                }
            }
        }
        //Here, we use items to display a list of items inside the
        //LazyColumn
        items(listData) { item ->
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = item.name)
            }
        }
    }
}

//Here, we create a preview function of the Home composable
@Preview(showBackground = true)
@Composable
fun PreviewHomeContent() {
    LAB_WEEK_09Theme {
        HomeContent(
            listData = mutableStateListOf(Student("Tanu"), Student("Tina"), Student("Tono")),
            inputField = Student("Example"),
            onInputValueChange = {},
            onButtonClick = {}
        )
    }
}
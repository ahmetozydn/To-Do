package com.ozaydin.todoapplication.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.ToDoModel
import com.ozaydin.todoapplication.theme.ToDoApplicationTheme

class DateTimePicker : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApplicationTheme {
                val navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   // MainScreen(navController = navController)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ToDoApplicationTheme {
        //MainScreen()
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var descriptionTextField = remember {
        mutableStateOf("")
    }
    //var viewModel: DateTimePickerViewModel = ViewModelProvider(this)[DateTimePickerViewModel::class.java]
    val title = remember {
        mutableStateOf(TextFieldValue())
    }
    val description = remember {
        mutableStateOf(TextFieldValue())
    }
    val calendarState = rememberSheetState()
    CalendarDialog(state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            Log.d("SelectedDate: ", "$date")
        })
    val clockState = rememberSheetState()
    ClockDialog(state = clockState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            Log.d("SelectedTime", "$hours : $minutes")
        }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Task")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home")
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(20.dp, 0.dp),
                    value = title.value,
                    label = { Text(text = "Title") },
                    onValueChange = {
                        title.value = it
                    },
                    placeholder = { Text("Enter the title") },
                    singleLine = true


                    /*           label = "Description",
                               placeholder = "Not compulsory"*/
                )
                Spacer(modifier = Modifier.height(12.dp))
                SpecialOutlinedTextField(descriptionTextField.value) {
                    descriptionTextField.value = it
                }


                Spacer(modifier = Modifier.height(16.dp))

                Row() {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            calendarState.show()
                        }, modifier = Modifier.padding(12.dp, 0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Pick a Date")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        clockState.show()
                    }, shape = RoundedCornerShape(8.dp)) {
                        Text(text = "Pick a Time")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Date: Time: ")
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {

                    },
                    modifier = Modifier.height(45.dp).fillMaxWidth().padding(20.dp, 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Gray,
                    ),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(2.dp, Color.Black)
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.vc_done),
                        contentDescription = "null",
                        tint = Color.Black,
                    )
                    Text(
                        text = "SAVE TASK",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .weight(1f)
                            .offset(x = -12.dp) //default icon width = 24.dp
                    )
                }
            }
        })





}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialOutlinedTextField(string : String, function: (String)  -> Unit){
    OutlinedTextField(
        modifier = Modifier.height(160.dp).fillMaxWidth().padding(20.dp, 0.dp),
        value = string  ,
        label = { Text(text = "Description") },
        onValueChange = function,

        placeholder = { Text("Enter the description") },
        maxLines = 5

        /*           label = "Description",
                   placeholder = "Not compulsory"*/
    )
}



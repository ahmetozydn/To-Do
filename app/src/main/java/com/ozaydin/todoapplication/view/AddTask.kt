package com.ozaydin.todoapplication.view

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.theme.CustomGray
import com.ozaydin.todoapplication.theme.DarkGreen
import com.ozaydin.todoapplication.utils.Util.Companion.CHANNEL_ID
import com.ozaydin.todoapplication.utils.Util.Companion.CHANNEL_NAME
import com.ozaydin.todoapplication.viewmodel.ViewModel
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, viewModel: ViewModel) {

    val context = LocalContext.current
    var selectedDate = ""
    var selectedTime = ""
    //val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.", Locale.ENGLISH)
    val descriptionTextField = remember {
        mutableStateOf("")
    }
    val titleTextField = remember {
        mutableStateOf("") //mutableStateOf(TextFieldValue())
    }
    val calendarState = rememberSheetState()
    CalendarDialog(state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            selectedDate = date.toString()
            println("DATE:  $date")
            println(selectedDate)
        })
    val clockState = rememberSheetState()
    ClockDialog(state = clockState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            println("TIME IN HOURS AND MINUTES: , $hours : $minutes")
            selectedTime = "$hours:$minutes"
            println(selectedTime)
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
                        // navController.navigate("task_list")
                        navController.popBackStack()
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
                    value = titleTextField.value,
                    label = { Text(text = "Title") },
                    onValueChange = {
                        titleTextField.value = it
                    },
                    placeholder = { Text("Enter the title") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = CustomGray)


                    /*           label = "Description",
                               placeholder = "Not compulsory"*/
                )
                Spacer(modifier = Modifier.height(12.dp))
                SpecialOutlinedTextField(descriptionTextField.value) {
                    descriptionTextField.value = it
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.padding(20.dp, 0.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            calendarState.show()
                        }, modifier = Modifier.padding(12.dp, 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.vc_time),
                            contentDescription = "null",
                            tint = Color.White,
                            modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp)
                        )
                        Text(text = "Due Date")
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            clockState.show()
                        },
                        modifier = Modifier.padding(12.dp, 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = Color.White,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.vc_date),
                            contentDescription = "null",
                            tint = Color.White,
                            modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp)
                        )
                        Text(text = "Due Time")
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))
                Text("Date: Time: ")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val capitalize = descriptionTextField.value.capitalized()
                        val capitalizedTitle = titleTextField.value.capitalized()
                        //var date = LocalDate.parse(selectedDate) // "dd-MM-yyyy"
                        //val id = UUID.randomUUID().toString() unique id
                        val aTask = Task(
                            capitalizedTitle.trim(),
                            capitalize.trim(),
                            selectedDate,
                            selectedTime,
                            false
                        )
                        viewModel.saveTask(aTask)
                        println(aTask)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // API 26
                            val channelId = CHANNEL_ID
                            val channelName = CHANNEL_NAME
                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                            val channel = NotificationChannel(channelId, channelName, importance)
                            val notificationManager =
                                context.getSystemService(NotificationManager::class.java)
                            notificationManager.createNotificationChannel(channel)
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // min API level is 31
                            }*/
                            createChannel(channelId, context, aTask)
                        }
                        navController.navigate("task_list")
                    },
                    modifier = Modifier.height(60.dp).fillMaxWidth().padding(20.dp, 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = DarkGreen,
                    ),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(0.dp, Color.Black)
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.vc_done),
                        contentDescription = "null",
                        tint = Color.White,
                    )
                    Text(
                        text = "SAVE",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .offset(x = (-12).dp) //default icon width = 24.dp
                    )
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialOutlinedTextField(string: String, function: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.height(160.dp).fillMaxWidth().padding(20.dp, 0.dp)
            .verticalScroll(rememberScrollState()),
        value = string,
        label = { Text(text = "Description") },
        onValueChange = function,

        placeholder = { Text("Enter the description") },

        maxLines = 5,
        colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = CustomGray)

        /*           label = "Description",
                   placeholder = "Not compulsory"*/
    )
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())



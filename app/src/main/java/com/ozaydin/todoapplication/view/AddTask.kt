package com.ozaydin.todoapplication.view

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ozaydin.todoapplication.theme.DarkGreen
import com.ozaydin.todoapplication.utils.Util.Companion.CHANNEL_NAME
import com.ozaydin.todoapplication.utils.capitalized
import com.ozaydin.todoapplication.utils.createChannel
import com.ozaydin.todoapplication.viewmodel.ViewModel
import java.util.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import com.ozaydin.todoapplication.theme.CustomGray
import com.ozaydin.todoapplication.theme.CustomGreen
import com.ozaydin.todoapplication.utils.getUniqueId

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, viewModel: ViewModel) {
    val showDialog = remember { mutableStateOf(false) }
    var isTextFieldEmpty by remember { mutableStateOf(false) }
    var isDueDateAdded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val selectedDate = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("") }
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
            selectedDate.value = date.toString()
            println("DATE:  $date")
            println(selectedDate)
        })
    val clockState = rememberSheetState()
    ClockDialog(state = clockState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            println("TIME IN HOURS AND MINUTES: , $hours : $minutes")
            selectedTime.value = "$hours:$minutes"
            println(selectedTime)
        }
    )
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.fillMaxWidth(),
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
                        if (isTextFieldEmpty) {
                            isTextFieldEmpty = false
                        }
                    },
                    isError = isTextFieldEmpty,
                    placeholder = { Text("Enter the title") },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)


                    /*           label = "Description",
                               placeholder = "Not compulsory"*/
                )
                Spacer(modifier = Modifier.height(12.dp))
                SpecialOutlinedTextField(descriptionTextField.value) {
                    descriptionTextField.value = it
                }
                DropDownMenu()

                Spacer(modifier = Modifier.height(16.dp))



                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.Start).padding(10.dp, 0.dp)
                ) {
                    AddDateTimeCheckBox(
                        checked = isDueDateAdded,
                        onCheckedChange = { isDueDateAdded = it }
                    )
                }
                Column {
                    AnimatedVisibility(isDueDateAdded) {
                        Row(
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    calendarState.show()
                                }, modifier = Modifier,
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedDate.value != "") DarkGreen else Color.Gray,
                                    contentColor = Color.White,
                                )
                            ) {
                                Column {
                                    Icon(
                                        painter = painterResource(id = R.drawable.vc_time),
                                        contentDescription = "null",
                                        tint = Color.White,
                                        modifier = Modifier.padding(0.dp, 12.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Text(text = "Due Date")
                                }

                            }
                            Button(
                                onClick = {
                                    clockState.show()
                                },
                                modifier = Modifier,
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTime.value != "") DarkGreen else Color.Gray,
                                    contentColor = Color.White,
                                )
                            ) {
                                Column {
                                    Icon(
                                        painter = painterResource(id = R.drawable.vc_date),
                                        contentDescription = "null",
                                        tint = Color.White,
                                        modifier = Modifier.padding(0.dp, 12.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Text(text = "Due Time")
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val capitalize = descriptionTextField.value.capitalized()
                            val capitalizedTitle = titleTextField.value.capitalized()
                            //var date = LocalDate.parse(selectedDate) // "dd-MM-yyyy"
                            //val id = UUID.randomUUID().toString() unique id

                            if (titleTextField.value.isEmpty()) {
                                isTextFieldEmpty = true
                                return@Button
                            } else {
                                isTextFieldEmpty = false
                            }
                            if (isDueDateAdded) {
                                if (selectedDate.value != "" && selectedTime.value != "") {
                                    val aTask = Task(
                                        capitalizedTitle.trim(),
                                        capitalize.trim(),
                                        selectedDate.value,
                                        selectedTime.value,
                                        false,
                                        "",
                                        getUniqueId()
                                    )
                                    viewModel.saveTask(aTask)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // API 26
                                        val channelId = getUniqueId()
                                        val channelName = CHANNEL_NAME
                                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                                        val channel =
                                            NotificationChannel(
                                                channelId.toString(),
                                                channelName,
                                                importance
                                            )
                                        val notificationManager =
                                            context.getSystemService(NotificationManager::class.java)
                                        notificationManager.createNotificationChannel(channel)
                                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // min API level is 31
                                        }*/
                                        createChannel(channelId.toString(), context, aTask)
                                    }
                                    navController.navigate("task_list")
                                } else {
                                    //ToastMessage("Please Enter Date and Time or Uncheck.")
                                    Toast.makeText(
                                        context,
                                        "Please, consider to add a due date and time or uncheck!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //showDialog.value = true
                                }
                            } else {
                                val aTask = Task(
                                    capitalizedTitle.trim(),
                                    capitalize.trim(),
                                    "",
                                    "",
                                    false
                                )
                                //save without due date
                                viewModel.saveTask(aTask)
                                navController.navigate("task_list")
                            }
                        },
                        modifier = Modifier.height(60.dp).fillMaxWidth().padding(20.dp, 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGreen,
                            contentColor = DarkGreen,
                        ),
                        shape = RoundedCornerShape(5.dp),
                        border = if (isTextFieldEmpty) BorderStroke(
                            2.dp,
                            Color.Red
                        ) else BorderStroke(0.dp, Color.Black)
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
        colors = TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White)
        /*           label = "Description",
                   placeholder = "Not compulsory"*/
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu() {

    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("HIGH", "LOW", "MEDIUM", "DEFAULT")
    var selectedText by remember { mutableStateOf("DEFAULT") }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier
                .wrapContentSize()
                .onGloballyPositioned { coordinates ->
                    //This value is us ed to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            readOnly = true,
            label = {Text("Priority")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }}
@Composable
fun AddDateTimeCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Checkbox(

            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 2.dp),
        )
        Text(
            text = "Remember me",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontSize = 16.sp,
        )
    }
}

@Composable
fun ToastMessage(text: String) { // TODO(show a snackbar instead of toast message when due date isn't entered.)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    SnackbarHost(hostState = snackbarHostState) {
        Snackbar(
            action = {
                IconButton(onClick = { snackbarHostState.currentSnackbarData?.dismiss() }) {
                    Icon(Icons.Filled.Close, contentDescription = "close")
                }
            },
            content = { Text(text) },
            modifier = Modifier.padding(16.dp)
        )
    }
    LaunchedEffect(text) {
        snackbarHostState.showSnackbar(text, duration = SnackbarDuration.Short)
    }
}





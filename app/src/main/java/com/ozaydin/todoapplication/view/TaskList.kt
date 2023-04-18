package com.ozaydin.todoapplication.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.transition.Visibility
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.*
import com.ozaydin.todoapplication.notification.AlarmReceiver
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.theme.*
import com.ozaydin.todoapplication.utils.Util.Companion.NOTIFICATION
import com.ozaydin.todoapplication.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TaskList : ComponentActivity() {
    //@Inject lateinit var taskListViewModel: TaskListViewModel
    private val taskListViewModel: ViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                    }

                } else {
                    //permission denied
                    Toast.makeText(this, "Permission needed!", Toast.LENGTH_LONG).show()
                }

            }
        setContent {
            ToDoApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "task_list"
                ) {
                    composable("task_list") {
                        TaskListScreen(navController = navController, taskListViewModel)
                    }
                    composable("add_task") { backStackEntry ->
                        //val itemId = backStackEntry.arguments?.getString("itemId")
                        AddTaskScreen(navController = navController, taskListViewModel)
                    }
                }
            }
        }
    }
/*    private fun onStatusChanged() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) { // android decides here
                Snackbar {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                }
            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            return
        } else {
            //permission granted

        }
    }*/
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskListScreen(navController: NavController, viewModel: ViewModel) {
    LaunchedEffect(key1 = true){
        viewModel.fetchTasks()
    }
    var isSearchResultEmpty by remember { viewModel.isSeachListEmpty }
    val isListEmpty by remember { viewModel.isListEmpty }
    val itemsList by viewModel._taskList

    GenerateList(viewModel, navController, isListEmpty, itemsList,isSearchResultEmpty)

    /* Box(contentAlignment = Alignment.Center,
         modifier = Modifier.fillMaxSize()){

             Box(
                 contentAlignment = Alignment.Center,
                 modifier = Modifier.fillMaxSize()
             ) {
                 ShowEmptyListMessage()
         }
     }*/

    //RequestPermission(permission = Manifest.permission.POST_NOTIFICATIONS)

    /* val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         rememberPermissionState(
             android.Manifest.permission.POST_NOTIFICATIONS
         )
     } else {
         TODO("VERSION.SDK_INT < TIRAMISU")
     }*/

    /*LaunchedEffect(key1 = true) { // unit
        println("launched effect triggered")
        viewModel.fetchTasks()
    }*/
    /*LaunchedEffect(key1 = true){
         notificationPermissionState.launchPermissionRequest()
     }

     if (notificationPermissionState.status.isGranted) {
         Text("Camera permission Granted")
     } else {
         Column {
             val textToShow = if (notificationPermissionState.status.shouldShowRationale) {
                 "The camera is important for this app. Please grant the permission."
             } else {
                 "Camera permission required for this feature to be available. " +
                         "Please grant the permission"
             }
             Text(textToShow)
             Button(
                 onClick = {
                     //notificationPermissionState.launchPermissionRequest()
                     println("onclicked***************************")
                 }, shape = RoundedCornerShape(8.dp),
                 colors = ButtonDefaults.buttonColors(
                     containerColor = DarkGreen,
                     contentColor = Color.White,
                 )
             ) {
                 Text(text = "Pick a Time")
             }
         }
     }*/
}


@Composable
fun SearchResultEmpty(){
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(3.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(
                width = 1.dp,
                color = CustomGray
            ),

            ) {
            Icon(
                painterResource(id = R.drawable.vc_seach_empty_result),
                "empty list icon",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                "No Search Result!",
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                fontSize = 20.sp,
                style = TextStyle.Default,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
        println("inside list is empty")

    }
}






@Composable
fun ShowEmptyListMessage() {
    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(3.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = BorderStroke(
                width = 1.dp,
                color = CustomGray
            ),

            ) {
            Icon(
                painterResource(id = R.drawable.vc_empty_list),
                "empty list icon",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                "Oops! No Tasks Found. Please try to add new one.",
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                fontSize = 24.sp,
                style = TextStyle.Default,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
        println("inside list is empty")

    }
}

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun GenerateList(
    viewModel: ViewModel,
    navController: NavController,
    isListEmpty: Boolean?,
    itemsList: List<Task>,
    isSearchResultEmpty : Boolean?
) {
    /*LaunchedEffect(Unit) {
        viewModel.fetchTasks()
    }*/
    println("Generate List Triggered")

    /* AnimatedVisibility(!isListEmpty) {
         Column(modifier = Modifier.background(Color.Green)) {

             SearchBar(
                 hint = "Search...",
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(0.dp),
             ) {
                 viewModel.searchList(it)
             }
             LazyColumn(
                 //state = lazyListState,
                 modifier = Modifier.fillMaxSize(),
                 contentPadding = PaddingValues(4.dp),

                 ) {


                 itemsIndexed(items = itemsList,
                     key = { index: Int, item: Task -> item.hashCode() },
                     itemContent = { _, item ->
                         *//*LaunchedEffect(key1 = Unit, block = {
                        snapshotFlow { state.offset.value }
                            .collect {
                                willDismissDirection = when {
                                    it > width * startActionsConfig.threshold -> DismissDirection.StartToEnd
                                    it < -width * endActionsConfig.threshold -> DismissDirection.EndToStart
                                    else -> null
                                }
                            }
                    })*//*
                        val currentItem by rememberUpdatedState(item)
                        val dismissState = rememberDismissState(confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                println("item removed****************")
                                viewModel.deleteTask(currentItem)
                                *//*viewModel._taskList.remove(currentItem)
                            listItems.remove(currentItem)*//*
                                //listItems.remove(currentItem)
                                //itemList = itemList.filter { it != item }.toMutableList()
                                //isListEmpty = listItems.value.isEmpty()
                                true
                            } else {
                                //isListEmpty = listItems.value.isEmpty()
                                false
                            }
                        })
                        *//*LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == DismissValue.DismissedToStart) {
                            dismissState.snapTo(DismissValue.DismissedToStart)
                        }
                    }*//*

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(EndToStart),
                            modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                            background = {
                                //val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                                val color by animateColorAsState(
                                    targetValue =
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.LightGray
                                        DismissValue.DismissedToStart -> CustomRed
                                        else -> Color.Transparent
                                    }
                                )
                                val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 1f else 1.6f)
                                *//*Box(
                                contentAlignment = alignment,
                                modifier = Modifier.fillParentMaxSize().background(color = color, shape = RoundedCornerShape(12.dp)).padding(4.dp),
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Icon",
                                    modifier = Modifier.scale(scale = scale).padding(12.dp),
                                    tint = Color.White,
                                    )
                            }*//*
                                Row(
                                    modifier = Modifier.fillParentMaxSize().padding(3.dp)
                                        .background(
                                            color = color,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Icon",
                                        modifier = Modifier.scale(scale = scale).padding(12.dp),
                                        tint = Color.White,
                                    )
                                }
                            },

                            dismissContent = {
                                GenerateCard(item) {}
                            }
                        )

                    }
                )

            }
        }
    }*/

    Column(modifier = Modifier.background(Color.White)) {
        Column {
            AnimatedVisibility(isListEmpty != null && isListEmpty == false) {
                SearchBar(
                    hint = "Search...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                ){
                    viewModel.searchList(it)
                }
            }
            AnimatedVisibility(isSearchResultEmpty != null  && isSearchResultEmpty == true){
                SearchResultEmpty()
            }
        }

        if(isListEmpty != null && isListEmpty == false) {
            LazyColumn(
                //state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
            ) {
                itemsIndexed(items = itemsList,
                    key = { index: Int, item: Task -> item.hashCode() },
                    itemContent = { _, item ->
                        /*LaunchedEffect(key1 = Unit, block = {
                        snapshotFlow { state.offset.value }
                            .collect {
                                willDismissDirection = when {
                                    it > width * startActionsConfig.threshold -> DismissDirection.StartToEnd
                                    it < -width * endActionsConfig.threshold -> DismissDirection.EndToStart
                                    else -> null
                                }
                            }
                    })*/
                        val currentItem by rememberUpdatedState(item)
                        val dismissState = rememberDismissState(confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                println("item removed****************")
                                viewModel.deleteTask(currentItem)
                                /*viewModel._taskList.remove(currentItem)
                            listItems.remove(currentItem)*/
                                //listItems.remove(currentItem)
                                //itemList = itemList.filter { it != item }.toMutableList()
                                //isListEmpty = listItems.value.isEmpty()
                                true
                            } else {
                                //isListEmpty = listItems.value.isEmpty()
                                false
                            }
                        })
                        /*LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == DismissValue.DismissedToStart) {
                            dismissState.snapTo(DismissValue.DismissedToStart)
                        }
                    }*/

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(EndToStart),
                            modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                            background = {
                                //val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                                val color by animateColorAsState(
                                    targetValue =
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.LightGray
                                        DismissValue.DismissedToStart -> CustomRed
                                        else -> Color.Transparent
                                    }
                                )
                                val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 1f else 1.6f)
                                /*Box(
                                contentAlignment = alignment,
                                modifier = Modifier.fillParentMaxSize().background(color = color, shape = RoundedCornerShape(12.dp)).padding(4.dp),
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Icon",
                                    modifier = Modifier.scale(scale = scale).padding(12.dp),
                                    tint = Color.White,
                                    )
                            }*/
                                Row(
                                    modifier = Modifier.fillParentMaxSize().padding(3.dp)
                                        .background(
                                            color = color,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Icon",
                                        modifier = Modifier.scale(scale = scale).padding(12.dp),
                                        tint = Color.White,
                                    )
                                }
                            },

                            dismissContent = {
                                GenerateCard(item) {}
                            }
                        )

                    }
                )
            }
        }
        AnimatedVisibility(isListEmpty != null && isListEmpty == true) {
            ShowEmptyListMessage()
        }

    }



    FloatingActionButtons(navController = navController)
    //val listItems = viewModel._taskList // remember gives MutableStateList
    /*SideEffect {
        println("Inside : Side Effect**********")
        viewModel.fetchTasks()
    }*/
    val lazyListState = rememberLazyListState()
    ///var list by remember { mutableStateListOf<Task>(emptyList())}//val list by remember{viewModel.taskList} // or directly use viewModel.taskList
    //var tasks = remember { viewModel._taskList } .toMutableStateList
    //var x = viewModel(TaskListViewModel::class.java)
    //val tasks = viewModel._taskList.value.toMutableStateList()
    //val tasks by remember { mutableStateListOf<Task>(emptyList())}
    //val tasks1: List<Task>? by viewModel._taskList.value.toMutableStateList()


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateCard(task: Task, onRemove: (Task) -> Unit) {
    val (year, month, day) = task.date!!.split("-").map {
        it.toInt()
    }
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, month - 1)
    val abbreviatedMonth = SimpleDateFormat("LLL", Locale.getDefault()).format(calendar.time)
    println("abbbbbbbbbbbbbbbbbbbbbbbbriviared time is :  $abbreviatedMonth")
    calendar.set(Calendar.MONTH, month)
    println("the calendar is +++++++++++++++01           :   $calendar")
    Card(
        modifier = Modifier.fillMaxSize().padding(3.dp)
            .shadow(1.dp, shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = CustomGray
        ),

        ) {
        if (task.title != null) {
            Text(
                modifier = Modifier.padding(12.dp, 4.dp, 2.dp, 2.dp),
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                maxLines = 1
            )
            Column {
                Text(
                    modifier = Modifier.padding(12.dp, 4.dp, 2.dp, 2.dp),
                    text = task.description!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 1,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(1f).padding(0.dp, 0.dp, 8.dp, 2.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.time?.let {
                        Text(
                            modifier = Modifier.padding(12.dp, 0.dp, 2.dp, 0.dp),
                            textAlign = TextAlign.End,
                            text = "$it $abbreviatedMonth $day, $year",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CustomGreen,
                            maxLines = 1,
                        )
                    }
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoApplicationTheme {
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    BasicTextField(
        modifier = modifier.padding(2.dp).fillMaxWidth(),
        value = text,
        maxLines = 1,
        onValueChange = {
            text = it
            onSearch(it)
        },
        cursorBrush = SolidColor(Color.Black),
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(4.dp) // margin left and right
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFD2F3F2),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFFAAE9E6),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .padding(all = 16.dp), // inner padding
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start

            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Favorite icon",
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.width(width = 8.dp))
                innerTextField()
            }
        }
    )
}


@SuppressLint("UnspecifiedImmutableFlag")
fun createChannel(channelId: String, context: Context, task: Task) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val (hours, min) = task.time!!.split(":").map { it.toInt() }
        val (year, month, day) = task.date!!.split("-")
            .map { it.toInt() } // if the date is saved as null, app always crushes
        val calendar = Calendar.getInstance()
        //calendar.set(Calendar.AM_PM, Calendar.AM);

        /*calendar.timeInMillis = System.currentTimeMillis()
        calendar.clear()*/
        calendar.set(year, month - 1, day, hours, min)
        if (calendar.timeInMillis >= System.currentTimeMillis()) {
            // Create the notification
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.vc_done)
                .setContentTitle(task.title)
                .setContentText(task.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setAutoCancel(true)
                .setVisibility(VISIBILITY_PUBLIC).build()
            println("the value of calendar is :  ${calendar.time}")
            // Create an intent to launch the notification
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(NOTIFICATION, notification)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31
                PendingIntent.getBroadcast(
                    context,
                    (0..100000).random(),
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, (0..100000).random(), intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            ) //was setExact
        }
    }
}

@Composable
fun FloatingActionButtons(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        FloatingActionButton(
            onClick = {
                //mContext.startActivity(Intent(mContext, DateTimePicker::class.java))
                navController.navigate("add_task")
            },
            shape = CircleShape,
            //backgroundColor = greenColor,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, "")
        }
    }
}

private fun createNotification(context: Context): Notification {
    return TODO()
}

@ExperimentalPermissionsApi
@Composable
fun RequestPermission(
    permission: String,
    deniedMessage: String = "Give this app a permission to proceed. If it doesn't work, then you'll have to do it manually from the settings.",
    rationaleMessage: String = "To use this app's functionalities, you need to give us the permission.",
) {
    val permissionState = rememberPermissionState(permission)

    HandleRequest(
        permissionState = permissionState,
        deniedContent = { shouldShowRationale ->
            PermissionDeniedContent(
                deniedMessage = deniedMessage,
                rationaleMessage = rationaleMessage,
                shouldShowRationale = shouldShowRationale,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        },
        content = {
            /*  Content(
                  text = "PERMISSION GRANTED!",
                  showButton = false
              ) {}*/
        }
    )
}

@ExperimentalPermissionsApi
@Composable
private fun HandleRequest(
    permissionState: PermissionState,
    deniedContent: @Composable (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    when (permissionState.status) {
        is PermissionStatus.Granted -> {
            content()
        }
        is PermissionStatus.Denied -> {
            deniedContent(permissionState.status.shouldShowRationale)
        }
    }
}

@Composable
fun Content(text: String, showButton: Boolean = true, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        if (showButton) {
            Button(
                onClick = onClick, colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    contentColor = DarkGreen,
                )
            ) {
                Text(text = "Request")
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun PermissionDeniedContent(
    deniedMessage: String,
    rationaleMessage: String,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {
    if (shouldShowRationale) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Permission Request",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(rationaleMessage)
            },
            confirmButton = {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGreen,
                        contentColor = DarkGreen,
                    ),
                ) {
                    Text("Give Permission")
                }
            }
        )
    } else {
        Content(text = deniedMessage, onClick = onRequestPermission)
    }
}




package com.ozaydin.todoapplication.presentation

import android.Manifest
import android.app.*
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowManager
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
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ozaydin.todoapplication.R
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.theme.*
import com.ozaydin.todoapplication.utils.cancelAlarm
import com.ozaydin.todoapplication.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
/*        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }*/
        //hide status bar

        window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
       // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContent {
            ToDoApplicationTheme {


                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash_screen"
                ) {
                    composable("splash_screen") {
                        AnimatedSplashScreen(navController) {
                            navController.navigate("task_list")
                        }
                        //AnimatedSplashScreen(navController)
                    }
                    composable("task_list") {
                        // show status bar

                        TaskListScreen(navController = navController, taskListViewModel)
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        //window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //window.decorView.setBackgroundColor(ContextCompat.getColor(this@TaskList, R.color.white));
                    }
                    composable("add_task",
                       // arguments = listOf(navArgument("task") { type = NavType.ParcelableType(Task::class.java) })
                        ) { backStackEntry ->
                        //val itemId = backStackEntry.arguments?.getString("itemId")
                        AddTaskScreen(navController = navController, taskListViewModel)
                    }
                }
            }
        }
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

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

data class CardItem(
    val word: String,
    val key: Int,
    var isDeleting: Boolean = false
)

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun Demo() {
    val cardItems = remember { mutableStateListOf<CardItem>() }

    // Adding items to the list
    cardItems.add(CardItem("Item 1", 0, false))
    cardItems.add(CardItem("Item 2", 1, false))
    cardItems.add(CardItem("Item 3", 2, false))
    cardItems.add(CardItem("item 4", 3, false))
    cardItems.add(CardItem("item 5", 4, false))
    cardItems.add(CardItem("item 6", 5, false))
    cardItems.add(CardItem("item 7", 6, false))
    cardItems.add(CardItem("item 8", 7, false))
    cardItems.add(CardItem("item 9", 8, false))
    cardItems.add(CardItem("item 10", 9, false))
    cardItems.add(CardItem("item 11", 10, false))
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        LazyColumn {
            items(cardItems) { cardItem ->

            }
        }

    }

}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TaskListScreen(navController: NavController, viewModel: ViewModel) {

    val systemUiController = rememberSystemUiController()
    // Set the status bar color
    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = true // Set true if your status bar icons should be dark, false if light
    )
    LaunchedEffect(key1 = true) {
        viewModel.fetchTasks()
    }
    var isSearchResultEmpty by remember { viewModel.isSeachListEmpty }
    val isListEmpty by remember { viewModel.isListEmpty }
    val itemsList by viewModel._taskList

    GenerateList(viewModel, navController, isListEmpty, itemsList, isSearchResultEmpty)

    var stringList = remember { mutableStateListOf<String>() }

    // Adding items to the list
    stringList.add("Item 1")
    stringList.add("Item 2")
    stringList.add("Item 3")
    stringList.add("item 4")

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
fun SearchResultEmpty() {
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
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp, 12.dp),
            )
            Text(
                "No Search Result!",
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                fontSize = 20.sp,
                style = TextStyle.Default,
                fontWeight = FontWeight.Normal,
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
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp),
            )
            Text(
                "Oops! No Tasks Found. Please try to add new one.",
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                fontSize = 24.sp,
                style = TextStyle.Default,
                fontWeight = FontWeight.Normal,
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
    isSearchResultEmpty: Boolean?
) {
    val context = LocalContext.current
    /*LaunchedEffect(Unit) {
        viewModel.fetchTasks()
    }*/
    println("Generate List Triggered")
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.background(Color.White)) {
        Column {
            AnimatedVisibility(isListEmpty != null && isListEmpty == false) {
                SearchBar(
                    hint = "Search...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                ) {
                    viewModel.searchList(it)
                }
            }
            AnimatedVisibility(isSearchResultEmpty != null && isSearchResultEmpty == true) {
                SearchResultEmpty()
            }
        }

        if (isListEmpty != null && isListEmpty == false) {
            LazyColumn(
                //state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
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
                                cancelAlarm(context = context, currentItem.alarmId)
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
                            modifier = Modifier.fillMaxWidth().animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = 700,
                                    easing = LinearOutSlowInEasing,
                                )
                            ),
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
                                    modifier = Modifier.fillParentMaxSize().padding(8.dp,4.dp)
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
                                        contentDescription = "Icon",
                                        modifier = Modifier.scale(scale = scale)
                                            .padding(0.dp, 0.dp, 12.dp, 0.dp),
                                        painter = painterResource(id = R.drawable.vc_done),
                                        tint = Color.White,
                                    )
                                }
                            },

                            dismissContent = {
                                GenerateCard(item,navController,currentItem,viewModel) {}
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
    FloatingActionButtons(navController = navController,viewModel)
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun GenerateCard(task: Task,navController: NavController, currentItem: Task,viewModel:ViewModel, onRemove: (Task) -> Unit) {
    var year = ""
    var month = ""
    var day = ""
    var abbreviatedMonth = ""
    var hours = ""
    var min = ""
    val calendar = Calendar.getInstance()
    if (!task.date.isNullOrBlank() && !task.time.isNullOrBlank()) {
        val dateList = task.date.split("-").map {// (year, month, day)
            it.toInt()
        }
        year = dateList[0].toString() // TODO consider to give a better solution
        month = dateList[1].toString()
        day = dateList[2].toString()
        calendar.set(Calendar.MONTH, month.toInt() - 1)
        abbreviatedMonth = SimpleDateFormat("LLL", Locale.getDefault()).format(calendar.time)
        println("abbbbbbbbbbbbbbbbbbbbbbbbriviared time is :  $abbreviatedMonth")
        calendar.set(Calendar.MONTH, month.toInt())
        println("the calendar is +++++++++++++++01           :   $calendar")
        val timeList = task.time.split(":").map {// (year, month, day)
            it.toInt()
        }
        if (timeList.isNotEmpty()) {
            hours = timeList[0].toString()
            min = timeList[1].toString()
            calendar.set(year.toInt(), month.toInt() - 1, day.toInt(), hours.toInt(), min.toInt())
        }
    }

    Card(
        modifier = Modifier.wrapContentHeight()
            .fillMaxWidth()
            .padding(8.dp, 4.dp)
            .clickable { viewModel.setClickedItem(currentItem)
                navController.navigate("add_task") } //3.dp
            .shadow(6.dp, shape = RoundedCornerShape(3.dp)), // 8dp
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(intrinsicSize = IntrinsicSize.Max)) {
            Box(
                Modifier.width(8.dp).fillMaxHeight().background(
                    if (!task.date.isNullOrBlank()) {
                        if (calendar.timeInMillis > System.currentTimeMillis()) DarkGreen else Color.Red
                    } else {
                        Color.Gray
                    }
                )
            ) {}

            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                Column {
                    Text(
                        modifier = Modifier.padding(8.dp, 4.dp, 4.dp, 0.dp),
                        text = task.title!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier.padding(8.dp, 4.dp, 4.dp, 0.dp),
                        text = task.description!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 1,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(1f).padding(8.dp, 4.dp, 8.dp, 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (task.date != "") {
                            Icon(
                                modifier = Modifier.size(24.dp).padding(0.dp),
                                painter = painterResource(id = R.drawable.vc_alarm),
                                contentDescription = "alarm icon",
                                tint = Color.Black
                            )
                        }
                        task.time?.let {
                            Text(
                                modifier = Modifier.padding(6.dp, 0.dp, 2.dp, 0.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.End,
                                text = if (day != "") "$it | $abbreviatedMonth $day, $year" else "",
                                style = MaterialTheme.typography.bodyMedium,
                                color =
                                if (calendar.timeInMillis >= System.currentTimeMillis()) DarkGreen else Color.Red,
                                maxLines = 1,
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(0.dp), contentAlignment = Alignment.CenterEnd
                        ) {
                            Button(
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.wrapContentWidth().height(20.dp)
                                    .defaultMinSize(
                                        minWidth = ButtonDefaults.MinWidth,
                                        minHeight = 1.dp
                                    ),
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (task.priority == "HIGH") PriorityHigh else if (task.priority == "LOW") PriorityLow else if (task.priority == "MEDIUM") PriorityMedium else Color.White,
                                    contentColor = Color.Black,
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ){
                                task.priority?.let {
                                    if (task.priority != "NONE") {
                                        Text(
                                            it.toLowerCase(),
                                            style = TextStyle(fontSize = 14.sp),
                                            modifier = Modifier.padding(0.dp),
                                            fontWeight = FontWeight.Bold,
                                            color = when(task.priority){
                                                "HIGH" -> PriorityHighTextColor
                                                "MEDIUM" -> PriorityMediumTextColor
                                                "LOW" -> PriorityLowTextColor
                                                else -> {Color.White }
                                            },
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                        }
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
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Transparent,
        backgroundColor = Color.Transparent
    )

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var isFocused = remember { false }
        BasicTextField(
            modifier = modifier.padding(8.dp,4.dp,8.dp,4.dp).fillMaxWidth()
                .shadow(1.dp, shape = RoundedCornerShape(16.dp)),
            value = text,
            maxLines = 1,
            onValueChange = {
                //if(it == "" && !isFocused) focusManager.clearFocus()
                text = it
                onSearch(it)
            },
            cursorBrush = if (isFocused) SolidColor(Color.Black) else SolidColor(Color.Transparent),
            singleLine = true,
            interactionSource = interactionSource,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(0.dp) // margin left and right
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFD2F3F2),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .focusModifier()
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused }
                        .border(
                            width = 2.dp,
                            color = Color(0xFFAAE9E6),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .padding(all = 20.dp), // inner padding
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


@Composable
fun FloatingActionButtons(navController: NavController,viewModel:ViewModel) {
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
                viewModel.setClickedItem(null)
                navController.navigate("add_task")
            },
            shape = CircleShape,
            //backgroundColor = greenColor,
            contentColor = Color.White
        ) {
            Icon(
                painterResource(R.drawable.vc_add_alarm),
                modifier = Modifier.size(24.dp),
                contentDescription = "",
                tint = Color.Black
            )
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

@Composable
fun AnimatedSplashScreen(navController: NavHostController, onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1000)
        navController.popBackStack()
        navController.navigate("task_list")
    }
    Splash(alpha = alphaAnim.value)
}

@Composable
fun Splash(alpha: Float) {
    Box(
        modifier = Modifier
            .background(Color.Black) //isSystemInDarkTheme()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(140.dp)
                .alpha(alpha = alpha),
            painter = painterResource(id = R.drawable.vc_done),
            contentDescription = "Logo Icon",
            tint = Color.Red
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    Splash(alpha = 1f)
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun SplashScreenDarkPreview() {
    Splash(alpha = 1f)
}





















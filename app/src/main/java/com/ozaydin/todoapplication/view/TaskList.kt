package com.ozaydin.todoapplication.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
// for a `var` variable also add
import androidx.compose.runtime.setValue
// or just
import androidx.compose.runtime.*
import android.view.View
import android.view.WindowInsets.Side
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.theme.*
import com.ozaydin.todoapplication.viewmodel.AddTaskViewModel
import com.ozaydin.todoapplication.viewmodel.TaskListViewModel
//import com.ozaydin.todoapplication.viewmodel.TaskListViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import kotlin.math.roundToInt

@AndroidEntryPoint
class TaskList : ComponentActivity() {
    //private val fooViewModel: TaskListViewModel by viewModels()
    //@Inject lateinit var taskListViewModel: TaskListViewModel
    private val taskListViewModel: TaskListViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            ToDoApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "task_list"
                ) {
                    //val taskListViewModel: TaskListViewModel by viewModels()

                    composable("task_list") {
                        //val viewModel: TaskListViewModel = viewModel() // by viewModels()
                        TaskListScreen(navController = navController, taskListViewModel)
                    }
                    composable("add_task") { backStackEntry ->
                        //val itemId = backStackEntry.arguments?.getString("itemId")
                        AddTaskScreen(navController = navController, taskListViewModel)
                    }
                }
                // A surface container using the 'background' color from the theme

            }
        }
    }
}

@Composable
fun TaskListScreen(navController: NavController, viewModel: TaskListViewModel) {

    // val owner = LocalViewModelStoreOwner.current

/*
    owner?.let { val viewModel: TaskListViewModel = viewModel(
            it,
            "TaskListViewModel",
            MainViewModelFactory(
                LocalContext.current.applicationContext
                        as Application
            )*/
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        //viewModel.fetchTasks()
        GenerateList(viewModel)
        FloatingActionButtons(navController)
    }
}

@Composable
fun FloatingActionButtons(navController: NavController) {
    val mContext = LocalContext.current

    // on the below line we are creating a column.
    Column(
        // on below line we are adding a modifier to it
        // and setting max size, max height and max width
        modifier = Modifier

            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(10.dp),

        // on below line we are adding vertical
        // arrangement and horizontal alignment.
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

@OptIn(
    ExperimentalMaterialApi::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun GenerateList(viewModel: TaskListViewModel) {
    LaunchedEffect(key1 = true) {
        println("launched effect***************")
        viewModel.fetchTasks()
    }
    val listItems = viewModel._taskList // remember
    listItems.value.forEach {
        println("title: ")
        println(it.title)
    }
    val items = remember {
        mutableStateListOf(
            Task("1", "lsjdfs", "", "", false),
            Task("2", "lsjadsfdfs", "", "", false),
            Task("3", "lsasdgjdfs", "", "", false),
            Task("4", "lsjfhgfdfs", "", "", false),
            Task("5", "lsjhdfs", "", "", false),
            Task("6", "lsjhadfs", "", "", false),
            Task("7", "lsjdfads", "", "", false),
            Task("8", "lsjadfs", "", "", false),
            Task("9", "lsjhgadfs", "", "", false),
            Task("10", "lsjhdfs", "", "", false),
            Task("11", "lsjgsadfs", "", "", false),
            Task("12", "lsjafdfs", "", "", false),
        )
    }
    val anotherList: SnapshotStateList<Task>
    /*SideEffect {
        println("Inside : Side Effect**********")
        viewModel.fetchTasks()
    }*/
    // var listItems = remember { mutableStateListOf<Task>() }
    val lazyListState = rememberLazyListState()

    viewModel._taskList.value.forEach {
        println("viewmodel._taskList : " + it.title)
    }
    /*LaunchedEffect("unit"){
        list = viewModel.taskList.value
    }*/
    ///var list by remember { mutableStateListOf<Task>(emptyList())}//val list by remember{viewModel.taskList} // or directly use viewModel.taskList
    //list = viewModel.taskList.value as SnapshotStateList<Task>
    //var tasks = remember { viewModel._taskList } .toMutableStateList
    //var x = viewModel(TaskListViewModel::class.java)
    //val tasks = viewModel._taskList.value.toMutableStateList()
    //val tasks by remember { mutableStateListOf<Task>(emptyList())}
    //val tasks1: List<Task>? by viewModel._taskList.value.toMutableStateList()
    Column {
        SearchBar(
            viewModel,
            hint = "Search...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
        ) {
            viewModel.searchList(it)
        }
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(4.dp),
        ) {
            itemsIndexed(items = listItems.value,
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
                            //viewModel._taskList.remove(currentItem)
                            //listItems.remove(currentItem)
                            viewModel.deleteTask(currentItem)
                            listItems.value = listItems.value.filter { it != item }.toMutableList()
                            true
                        } else false
                    })
                    /*LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue == DismissValue.DismissedToStart) {
                            dismissState.snapTo(DismissValue.DismissedToStart)
                        }
                    }*/
                    println("generating list... *************************")
                    val state = rememberDismissState(
                        confirmStateChange = {
                            when (it) {
                                DismissValue.DismissedToStart -> {
                                    viewModel.deleteTask(currentItem)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    )
                    SwipeToDismiss(

                        state = dismissState,
                        directions = setOf(EndToStart),
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                        background = {
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color by animateColorAsState(
                                targetValue =
                                when (dismissState.targetValue) {
                                    DismissValue.Default -> Color.LightGray
                                    DismissValue.DismissedToStart -> CustomRed
                                    else -> Color.Transparent
                                }
                            )
                            val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 1f else 1.6f)

                            val alignment = Alignment.CenterEnd
                            Box(
                                contentAlignment = alignment,
                                modifier = Modifier.fillParentMaxSize().background(color = color)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Icon",
                                    modifier = Modifier.scale(scale = scale).padding(12.dp),
                                    tint = Color.White
                                )
                            }
                        },

                        dismissContent = {
                            GenerateCard(item, state) {}
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateCard(task: Task, dismissState: DismissState, onRemove: (Task) -> Unit) {

    Card(
        modifier = Modifier.fillMaxSize().padding(2.dp).shadow(1.dp,shape = RoundedCornerShape(8.dp)), colors = CardDefaults.cardColors(
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
            Column() {
                Text(
                    modifier = Modifier.padding(12.dp, 4.dp, 2.dp, 2.dp),
                    text = task.description!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 1,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.time?.let {
                        Text(
                            modifier = Modifier.padding(12.dp, 0.dp, 2.dp, 0.dp),
                            textAlign = TextAlign.End,
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Blue,
                            maxLines = 1,
                        )
                    }
                    task.date?.let {
                        Text(
                            modifier = Modifier.padding(4.dp, 0.dp, 2.dp, 0.dp),
                            textAlign = TextAlign.Right,
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Blue,
                            maxLines = 1,
                        )
                    }
                }
            }
        }


    }
}

fun <T> SnapshotStateList<T>.swapList(newList: List<T>) {
    clear()
    addAll(newList)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoApplicationTheme {
    }
}

@Composable
fun SearchBar(
    viewModel: TaskListViewModel,
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

package com.ozaydin.todoapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ozaydin.todoapplication.data.ToDoModel
import com.ozaydin.todoapplication.ui.theme.ToDoApplicationTheme
import com.ozaydin.todoapplication.ui.theme.greenColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   /* LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {

                        *//*item {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(vertical = 25.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                            }
                        }*//*
                    }*/

                    //ViewModel, Mutablelivedata
                    val toDo1 = ToDoModel("go to grocery", "make the task today.",null,null,false)
                    val toDo2 = ToDoModel("an exam waiting for you", "prepare for the exam.",null,null,false)
                    val toDo3 = ToDoModel("pay the bills", "there are a few bills to pay.",null,null,false)

                    val toDoList = arrayListOf<ToDoModel>()
                    toDoList.add(toDo1)
                    toDoList.add(toDo2)
                    toDoList.add(toDo3)

                    List(toDoList)
                    FloatingActionButtons()
                }
            }
        }
    }
}
@Composable
fun FloatingActionButtons(){
    val ctx = LocalContext.current

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
                Toast.makeText(ctx, "Clicked", Toast.LENGTH_SHORT).show()
            },
             //backgroundColor = greenColor,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, "")
        }
    }
}
@Composable
fun List(toDos : List<ToDoModel>){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "\uD83C\uDF3F  TO DO",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        items(toDos) { toDo ->
            ToDoCard(toDo.title, toDo.description)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoCard(title: String?, description: String?) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium
        //elevation = 5.dp
        //background   = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(8.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = description!!,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoApplicationTheme {
        Greeting("Android")
    }
}
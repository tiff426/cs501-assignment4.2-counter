package com.example.counter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.example.counter.ui.theme.CounterTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: MyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            CounterTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
            Surface(
                modifier = Modifier.fillMaxSize().padding(32.dp), // Make the surface fill the entire screen
            ) {
                FullApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CounterTheme {
        Greeting("Android")
    }
}

class MyViewModel : ViewModel() {
    // using stateflow
    var count = MutableStateFlow(0)

    // for screen and auto
    var autoOn by mutableStateOf(true)
    var countScreen by mutableStateOf(true)

    // safely update
    fun increment() {
        count.update {
            currentValue -> currentValue + 1
        }
//        count.value++
    }
    fun decrement() {
        count.update {
                currentValue -> currentValue - 1
        }
    }

    fun reset() {
        count.update {
            currentValue -> 0
        }
    }

    fun toggleAuto() {
        autoOn = !autoOn
    }
    fun toggleScreen() {
        countScreen = !countScreen
    }

//    fun getCount(): Int {
//        return count.value
//    }
}

@Composable
fun Counter(viewModel: MyViewModel = MyViewModel(), autoOn: Boolean) {
   // val currCount = viewModel.count.collectAsState(initial = viewModel.getCount())
    val currCountState = viewModel.count.collectAsState()
    val currCount = currCountState.value
//    var autoOn by rememberSaveable { mutableStateOf(true) }

    // lifecycle aware coroutine = launched effect
    // should be lifecycle aware since not user induced changes
    if (autoOn) {
        LaunchedEffect(autoOn) {
            while (autoOn) {
                delay(3000) // coroutine
                viewModel.increment()
            }
        }
    }

    Column() {
        Text("$currCount")
        Button(onClick = { viewModel.increment() }) {
            Text("+1")
        }
        Button(onClick = { viewModel.decrement() }) {
            Text("-1")
        }
        Button(onClick = { viewModel.reset() }) {
            Text("reset")
        }
        Button(onClick={viewModel.toggleScreen()}) {
            Text("go to auto page")
        }
    }
}

@Composable
fun AutoScreen(viewModel: MyViewModel, autoOn: Boolean) {
    Column() {
        Text("Toggle auto increment")
        Button(onClick={viewModel.toggleAuto()}) {
            Text(if(autoOn){"turn off"}else{"turn on"})
        }
        Button(onClick={viewModel.toggleScreen()}) {
            Text("go to count page")
        }
    }
}

@Composable
fun FullApp(viewModel: MyViewModel = MyViewModel()) {
    if (viewModel.countScreen) {
        Counter(viewModel, viewModel.autoOn)
    } else {
        AutoScreen(viewModel, viewModel.autoOn)
    }

}
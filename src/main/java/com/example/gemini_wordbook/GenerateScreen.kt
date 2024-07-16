package com.example.gemini_wordbook

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GenerateScreen(
    wordViewModel: WordViewModel = viewModel()
) {
    var selectedNumber by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.title_gen),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Difficulty Settings",
                modifier = Modifier.padding(16.dp)
            )

            for (i in 9 downTo 1) {
                NumberButton(
                    number = i,
                    isSelected = selectedNumber == i,
                    onClick = {
                        //selectedNumber = i
                        wordViewModel.navigateToQuizScreen(i)
                    }
                )
            }

            Button(
                onClick = {
                    wordViewModel.navigateToWordScreen("")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.wordscreen_go))
            }
        }
    }


//            TextField(
//                value = prompt,
//                label = { Text(stringResource(R.string.label_prompt)) },
//
//                onValueChange = { prompt = it },
//                //onValueChange = { wordViewModel.updateInputText(it) },
//
//                modifier = Modifier
//                    .weight(0.8f)
//                    .padding(end = 16.dp)
//                    .align(Alignment.CenterVertically)
//            )

//        Column(modifier = Modifier.padding(all = 16.dp)) {
//            Button(
//                onClick = {
//                    wordViewModel.navigateToWordScreen()
//                },
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//            ) {
//                Text(text = stringResource(R.string.wordscreen_go))
//            }
//        }

}

@Composable
fun NumberButton(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(70.dp)
            .height(70.dp)
            .padding(4.dp),
        /*
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color.Blue else Color.Gray,
            contentColor = Color.White
        )*/
    ) {
        Text(number.toString())
    }
}

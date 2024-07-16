package com.example.gemini_wordbook

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun  MainApp(
    wordViewModel: WordViewModel = viewModel()
) {
    //Log.d(TAG, "currentScreen: " + wordViewModel.currentScreen.collectAsState().value.toString())

    when (val currentScreen = wordViewModel.currentScreen.collectAsState().value) {
        Screen.GenerateScreen -> GenerateScreen()
        Screen.QuizScreen -> QuizScreen(wordViewModel)
        Screen.WordScreen -> WordScreen(wordViewModel)
    }
}
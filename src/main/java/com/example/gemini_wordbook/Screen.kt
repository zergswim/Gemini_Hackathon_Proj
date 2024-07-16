package com.example.gemini_wordbook

sealed class Screen {
    object GenerateScreen : Screen()
    object WordScreen : Screen()
    object QuizScreen : Screen()
}
package com.example.gemini_wordbook

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordViewModel : ViewModel() {
  //네비게이션 부분
  //시작화면
  private val _currentScreen = MutableStateFlow<Screen>(Screen.GenerateScreen)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  //이동화면
  fun navigateToGenerateScreen() {
    _currentScreen.value = Screen.GenerateScreen
  }

  fun navigateToWordScreen(word: String) {
    _currentScreen.value = Screen.WordScreen
    if(word != "")
      sendPrompt(word)
  }

  fun navigateToQuizScreen(selectNumber: Int) {
    _currentScreen.value = Screen.QuizScreen
    sendDifficulty(selectNumber.toString())
    //Log.d(TAG, "selectedNo:" + selectNumber.toString())
  }

  //AI 호출응답 부분
  private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  val safety1 = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE) //.LOW_AND_ABOVE)
  val safety2 = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE)
  val safety3 = SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
  val safety4 = SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE)

  private val model_gen = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = BuildConfig.GeminiAI_apiKey,
    generationConfig = generationConfig {
      temperature = 1f
      topK = 64
      topP = 0.95f
      maxOutputTokens = 8192
      responseMimeType = "application/json"
    },
    safetySettings = listOf(safety1, safety2, safety3, safety4),
    systemInstruction = content {
      text(
        "We will play an English quiz game, and the number entered becomes the level of difficulty(1 to 5, easy to difficult) of the quiz game. Based on this difficulty level, create a sentence and hint that can associate an image in the same way as the example. (However, the word must be randomly choice of 1000 words. the hint must not contain the selected word. The third hint is the length of the word.) ex) {\"level\": 1, \"word\": \"apple\", \"image\": \"A red, round fruit with a green stem.\", \"hint1\": \"It's a popular fruit often associated with the fall season.\", \"hint2\": \"It's said to have fallen on Isaac Newton's head, inspiring his theory of gravity.\", \"hint3\": \"5\"}"
      )
    }
  )

  fun sendDifficulty(
    prompt: String
  ) {
    _uiState.value = UiState.Loading(prompt)

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val response = model_gen.generateContent(
          content {
            text(prompt)
          }
        )
        response.text?.let {
          outputContent -> _uiState.value = UiState.Success(outputContent)
        }
      } catch (e: Exception) {
        _uiState.value = UiState.Error(e.localizedMessage ?: "")
      }
    }
  }

  private val model_quiz = GenerativeModel(
//    modelName = "gemini-pro-vision",
    modelName = "gemini-1.5-flash",
    apiKey = BuildConfig.GeminiAI_apiKey,
    generationConfig = generationConfig {
      temperature = 1f
      topK = 64
      topP = 0.95f
      maxOutputTokens = 8192
      responseMimeType = "application/json"
    },
    safetySettings = listOf(safety1, safety2, safety3, safety4),
    systemInstruction = content {
      text("When an English word is presented, it creates an image that is easy to associate with the word's origin and generates an example sentence. ex)  {\"word\": \"endorse\",\"etymology\": \"Latin in (in) + dorso (back) → to attach to the back → to support\", \"image\": \"Photo of a celebrity holding a product\", \"example\": \"The celebrity endorsed the new line of cosmetics.\"]}")
    }
  )

  fun sendPrompt(
    //bitmap: Bitmap,
    prompt: String
  ) {
    _uiState.value = UiState.Loading(prompt)

    viewModelScope.launch(Dispatchers.IO) {
      try {
        val response = model_quiz.generateContent(
          content {
            //image(bitmap)
            text(prompt)
          }
        )
        response.text?.let { outputContent ->
          _uiState.value = UiState.Success(outputContent)
        }
      } catch (e: Exception) {
        _uiState.value = UiState.Error(e.localizedMessage ?: "")
      }
    }
  }
}
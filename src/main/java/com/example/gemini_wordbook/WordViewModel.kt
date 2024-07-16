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
        "I'm going to make an English vocabulary quiz. The level of difficulty is set according to the number presented at the highest 9 levels of difficulty, and one word out of 100 English words corresponding to that level of difficulty is randomly selected to express an image that can easily be associated with the word, and to guess this English word. Please create the 3 hints in Korean. (However, the hint must not contain an English word. The third hint is the number of the word.) ex) {\"word\": \"discretion\", \"image\": \"A judge's gavel, symbolizing a decision being made\", \"hint1\": \"신중하고 현명하게 판단하는 능력을 뜻해.\", \"hint2\":\"비밀을 지키고 함부로 말하지 않는 것을 의미해. \", \"hint3\":\"10\"}"
      )
    }
  )

  fun sendDifficulty(
    prompt: String
  ) {
    _uiState.value = UiState.Loading

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
    _uiState.value = UiState.Loading

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
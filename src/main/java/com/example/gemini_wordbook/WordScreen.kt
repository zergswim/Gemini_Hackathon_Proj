package com.example.gemini_wordbook

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WordScreen(
  wordViewModel: WordViewModel = viewModel()
) {
  val placeholderPrompt = stringResource(R.string.prompt_placeholder)
  val placeholderResult = stringResource(R.string.results_placeholder)
  var prompt by remember { mutableStateOf(placeholderPrompt) }
  var result by remember { mutableStateOf(placeholderResult) }
  val imageUrls = remember { mutableStateListOf<String>() }

  val context = LocalContext.current
  val ttsManager = remember { TextToSpeechManager(context) }
  val uiState by wordViewModel.uiState.collectAsState()
  var ttsOk by remember { mutableStateOf(false) }

  DisposableEffect(key1 = ttsManager) {
    onDispose {
      ttsManager.shutdown()
    }
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = "Find a word",
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(16.dp)
    )

    Row(
      modifier = Modifier.padding(all = 16.dp)
    ) {
      TextField(
        value = prompt,
        label = { Text(stringResource(R.string.label_prompt)) },

        onValueChange = { prompt = it },
        //onValueChange = { wordViewModel.updateInputText(it) },

        modifier = Modifier
          .weight(0.8f)
          .padding(end = 16.dp)
          .align(Alignment.CenterVertically)
      )

      Button(
        onClick = {
          imageUrls.clear()
          wordViewModel.sendPrompt(prompt)
        },
        enabled = prompt.isNotEmpty(),
        modifier = Modifier
          .align(Alignment.CenterVertically)
      ) {
        Text(text = stringResource(R.string.action_go))
      }
    }

    when (val currentState = uiState) {
      is UiState.Initial -> {
        // 초기 상태 처리
        Text(
          text = "Enter a word to get started",
          modifier = Modifier
            .padding(16.dp)
            .align(Alignment.CenterHorizontally)
        )
      }
      is UiState.Loading -> {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        ttsOk = true
        //Log.d(TAG, "loading ttsOk: " + ttsOk.toString())
      }
      is UiState.Error -> {
        Text(
          text = currentState.errorMessage,
          color = MaterialTheme.colorScheme.error,
          modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(16.dp)
        )
      }
      is UiState.Success -> {
        val result = currentState.outputText

        val jsonObject = JSONObject(result)
        val etymology = jsonObject.optString("etymology", "Not available")
        val example = jsonObject.optString("example", "Not available")

        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ){
          Text(
            text = "어원: $etymology",
            modifier = Modifier.padding(16.dp)
          )

          Text(
            text = "예문: $example",
            modifier = Modifier.padding(16.dp)
          )

          if(ttsOk) {
            //Log.d(TAG, "ttsManager 작동")
            ttsManager.speak(etymology)
            ttsManager.speak(example)
            ttsOk = false
          }

          val image_txt = jsonObject.optString("image", "")

          Text(
            text = "시각화: $image_txt",
            modifier = Modifier.padding(16.dp)
          )

          LaunchedEffect(image_txt) {
            val fetchedImageUrls = fetchImageUrls(image_txt)
            Log.d("WordScreen", "Fetched image URLs: $fetchedImageUrls")
            imageUrls.clear()
            imageUrls.addAll(fetchedImageUrls)
          }

          LazyRow(
            modifier = Modifier.padding(4.dp)
          ) {
            items(imageUrls) { imageUrl ->
              AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                  .padding(4.dp)
                  .size(200.dp, 200.dp) // 고정 크기 지정
                  .clip(RoundedCornerShape(8.dp)) // 모서리 둥글게
              )
            }
          }
        }
      }
    }
  }
}
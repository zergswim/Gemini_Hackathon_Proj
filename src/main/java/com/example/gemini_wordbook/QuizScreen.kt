package com.example.gemini_wordbook

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import kotlin.random.Random

@Composable
fun QuizScreen(
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
  var answer by remember { mutableStateOf("") }

  DisposableEffect(key1 = ttsManager) {
    onDispose {
      ttsManager.shutdown()
    }
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    Text(
      text = stringResource(R.string.title),
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
          if(answer == prompt)
          {
            Toast.makeText(context, "Correct.", Toast.LENGTH_SHORT).show()
            wordViewModel.navigateToGenerateScreen()
          }
          else
            Toast.makeText(context, "Wrong.", Toast.LENGTH_SHORT).show()

        },
        enabled = prompt.isNotEmpty(),
        modifier = Modifier
          .align(Alignment.CenterVertically)
      ) {
        Text(text = stringResource(R.string.action_answer))
      }
    }

    when (val currentState = uiState) {
      is UiState.Initial -> {
        // 초기 상태 처리
        Text(
          text = "Enter the answer",
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
//        Log.d(TAG, "result:" + result.toString())
//
//        Text(
//          text = "result: $result",
//          modifier = Modifier.padding(16.dp)
//        )

        val jsonObject = JSONObject(result)
        answer = jsonObject.optString("word", "")
        val hint1 = jsonObject.optString("hint1", "Not available")
        val hint2 = jsonObject.optString("hint2", "Not available")
        val hint3 = jsonObject.optString("hint3", "Not available")
        val image_txt = jsonObject.optString("image", "Not available")

        LaunchedEffect(image_txt) {
          val fetchedImageUrls = fetchImageUrls(image_txt)
          Log.d("WordScreen", "Fetched image URLs: $fetchedImageUrls")
          imageUrls.clear()
          imageUrls.addAll(fetchedImageUrls)
        }


        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ){
          Text(
            text = "Image: $image_txt",
            modifier = Modifier.padding(16.dp)
          )

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

          val (hintText1, setHint1) = remember { mutableStateOf("Click me to see the hint 1") }

          Button(
            onClick = { setHint1(hint1) },
            modifier = Modifier.padding(16.dp)
          ) {
            Text(hintText1)
          }

          val (hintText2, setHint2) = remember { mutableStateOf("Click me to see the hint 2") }

          Button(
            onClick = { setHint2(hint2) },
            modifier = Modifier.padding(16.dp)
          ) {
            Text(hintText2)
          }

          val (hintText3, setHint3) = remember { mutableStateOf("Click me to see the hint 3") }

          Button(
            onClick = { setHint3(hint3) },
            modifier = Modifier.padding(16.dp)
          ) {
            Text(hintText3)
          }

          val (hintText4, setHint4) = remember { mutableStateOf("Click me to see the answer") }

          Button(
            onClick = { setHint4(answer) },
            modifier = Modifier.padding(16.dp)
          ) {
            Text(hintText4)
          }

          Button(
            onClick = { wordViewModel.navigateToWordScreen(answer) },
            modifier = Modifier.padding(16.dp)
          ) {
            Text("Find")
          }
        }

      }
    }
  }
}

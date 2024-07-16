package com.example.gemini_wordbook

// TextToSpeechManager.kt
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

class TextToSpeechManager(context: Context) {
    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady
    private val speechQueue = LinkedBlockingQueue<String>()
    private var isSpeaking = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                _isReady.value = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}

                    override fun onDone(utteranceId: String?) {
                        isSpeaking = false
                        speakNext()
                    }

                    override fun onError(utteranceId: String?) {
                        isSpeaking = false
                        speakNext()
                    }
                })
            }
        }
    }

    fun speak(text: String) {
        speechQueue.offer(text)
        if (!isSpeaking) {
            speakNext()
        }
    }

    private fun speakNext() {
        if (_isReady.value && !isSpeaking) {
            speechQueue.poll()?.let { text ->
                isSpeaking = true
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
            }
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        speechQueue.clear()
    }
}
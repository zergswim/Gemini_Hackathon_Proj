package com.example.gemini_wordbook

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Json

data class SearchResult(
    @Json(name = "items") val items: List<Item>
)

data class Item(
    @Json(name = "title") val title: String,
    @Json(name = "link") val link: String
)

suspend fun fetchImageUrls(query: String): List<String> {
    val apiKey = BuildConfig.GoogleImgSearch_apiKey 
    val cx = BuildConfig.GoogleImgSearch_cx
    val url = "https://www.googleapis.com/customsearch/v1?q=$query&cx=$cx&num=3&searchType=image&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        try{
            if (responseBody != null) {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val adapter = moshi.adapter(SearchResult::class.java)
                val searchResult = adapter.fromJson(responseBody)
                searchResult?.items?.map { it.link } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception)
        {
            Log.d(TAG, "url: " + url)
            Log.d(TAG, "Exception: " + e.toString())
            emptyList()
        }
    }
}

fun main() = runBlocking {
    val query = "Kotlin programming"
    val imageUrls = fetchImageUrls(query)
    imageUrls.forEach { println(it) }
}

package com.example.aihelper

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:11434/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: AIService by lazy {
        retrofit.create(AIService::class.java)
    }
}


class AIRepository {
    suspend fun generateResponse(prompt: String, model: String): Flow<String> = flow {
        val response = RetrofitInstance.api.generateResponse(PromptRequest(prompt, model))
        if (response.isSuccessful) {
            val source = response.body()?.source() ?: return@flow
            while (!source.exhausted()) {
                val line = source.readUtf8Line()
                if (line != null) {
                    val responsePart = Gson().fromJson(line, PromptResponse::class.java)
                    emit(responsePart.response)
                    if (responsePart.done) break
                }
            }
        } else {
            emit("Error: ${response.errorBody()?.string()}")
        }
    }.flowOn(Dispatchers.IO)
}
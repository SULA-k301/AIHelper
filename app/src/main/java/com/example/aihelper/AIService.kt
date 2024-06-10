package com.example.aihelper

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface AIService {
    @POST("api/generate")
    @Streaming
    suspend fun generateResponse(@Body prompt: PromptRequest): Response<ResponseBody>
}
package com.example.aihelper

data class PromptResponse(
    val response: String,
    val done: Boolean,
    val model: String? = null,
    val created_at: String? = null,
    val context: List<Int>? = null,
    val total_duration: Long? = null,
    val load_duration: Long? = null,
    val prompt_eval_count: Int? = null,
    val prompt_eval_duration: Long? = null,
    val eval_count: Int? = null,
    val eval_duration: Long? = null
)

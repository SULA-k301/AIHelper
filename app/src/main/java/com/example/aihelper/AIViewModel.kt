package com.example.aihelper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AIViewModel(private val repository: AIRepository) : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response
    var isGenerationComplete = false
    private var generationJob: Job? = null

    fun sendPrompt(prompt: String, model: String) {
        generationJob?.cancel()
        isGenerationComplete = false
        _response.value = ""
        generationJob  = viewModelScope.launch {
            repository.generateResponse(prompt, model)
                .catch { e ->
                    _response.postValue("Error: ${e.message}")
                    isGenerationComplete = true
                }
                .collect { responsePart ->
                    _response.value += responsePart
                    isGenerationComplete = true
                }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
        isGenerationComplete = true
    }
}

class AIViewModelFactory(private val repository: AIRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIViewModel::class.java)) {
            return AIViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
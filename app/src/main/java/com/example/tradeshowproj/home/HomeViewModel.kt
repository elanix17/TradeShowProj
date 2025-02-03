package com.example.tradeshowproj.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class HomeViewModel() : ViewModel() {

    var successMessage = mutableStateOf("")
    var errorMessage = mutableStateOf("")


    fun sendPostRequest(deck: PostRequest) {
        viewModelScope.launch {
                Log.d("Network", "sendPostRequest: in launch")
                try {
                    Log.d("Network", "sendPostRequest: in try")

                    val response = ApiClient.apiService.createPost(deck)

                    if (response.isSuccessful) {
                        Log.d("Network", "sendPostRequest: is success")
                        successMessage.value = "Request successful: ${response.code()}"
                    } else {
                        Log.d("Network", "sendPostRequest: is failure")
                        errorMessage.value = "Request failed: ${response.code()}"
                    }
                } catch (e: Exception) {
                    Log.d("Network", "sendPostRequest: in exception")
                    errorMessage.value = e.message ?: "An error occurred"
                    e.printStackTrace()
                }
        }
    }
}

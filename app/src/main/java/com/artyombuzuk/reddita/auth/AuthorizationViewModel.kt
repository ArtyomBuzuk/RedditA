package com.artyombuzuk.reddita.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorizationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthorizationRepository(application)
    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.d("error", "$throwable")
        }
    private val scope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)

    fun toSharedPrefs() {
        scope.launch {
            repository.toSharedPrefs()
        }
    }
}
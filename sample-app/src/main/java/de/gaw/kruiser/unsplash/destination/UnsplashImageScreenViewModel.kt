package de.gaw.kruiser.unsplash.destination

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UnsplashImageScreenViewModel(private val url: String) : ViewModel() {
    data class Factory(private val url: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return UnsplashImageScreenViewModel(url = url) as T
        }
    }

    init {
        Log.v("UnsplashViewModel", "Init: $url")
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("UnsplashViewModel", "Disposed: $url")
    }
}
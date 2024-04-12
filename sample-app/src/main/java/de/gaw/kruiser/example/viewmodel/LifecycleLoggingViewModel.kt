package de.gaw.kruiser.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

abstract class LifecycleLoggingViewModel : ViewModel() {
    init {
        Log.v("KruiserSample", "ViewModel initialized: ${this.javaClass.simpleName}")
    }

    override fun onCleared() {
        Log.v("KruiserSample", "ViewModel cleared: ${this.javaClass.simpleName}")
        super.onCleared()
    }
}
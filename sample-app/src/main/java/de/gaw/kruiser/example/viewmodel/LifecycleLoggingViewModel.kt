package de.gaw.kruiser.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

abstract class LifecycleLoggingViewModel : ViewModel() {

    open fun name(): String = this.javaClass.simpleName

    init {
        Log.v("KruiserSample", "ViewModel initialized: ${this.name()}")
    }

    override fun onCleared() {
        Log.v("KruiserSample", "ViewModel cleared: ${this.name()}")
        super.onCleared()
    }
}
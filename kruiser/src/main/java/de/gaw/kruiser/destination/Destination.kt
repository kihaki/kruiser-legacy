package de.gaw.kruiser.destination

import android.os.Parcelable
import androidx.compose.runtime.Composable
import java.io.Serializable

/**
 * Shorthand for a parcelable Destination which makes sense on Android.
 */
interface AndroidDestination : Destination, Parcelable

interface Destination : Serializable {
    fun build(): Screen
}

/**
 * Lives only for as long as the screen is visible
 */
interface Screen {
    @Composable
    fun Content()
}
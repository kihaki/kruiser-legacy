package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.MutableBackstack

val LocalMutableBackstack = compositionLocalOf<MutableBackstack?> { null }
val LocalBackstack = compositionLocalOf<Backstack?> { null }
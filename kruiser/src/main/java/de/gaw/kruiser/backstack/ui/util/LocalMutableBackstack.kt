package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.MutableBackstack

val LocalMutableBackstack = compositionLocalOf<MutableBackstack?> { null }
val LocalBackstack = compositionLocalOf<Backstack?> { null }
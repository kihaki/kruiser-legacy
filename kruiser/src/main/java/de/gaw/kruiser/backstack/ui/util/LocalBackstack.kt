package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.MutableBackstack

val LocalBackstack = compositionLocalOf<MutableBackstack> { error("No LocalBackstack provided.") }
package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.core.MutableBackstackState

val LocalMutableBackstackState = compositionLocalOf<MutableBackstackState?> { null }
val LocalBackstackState = compositionLocalOf<BackstackState?> { null }
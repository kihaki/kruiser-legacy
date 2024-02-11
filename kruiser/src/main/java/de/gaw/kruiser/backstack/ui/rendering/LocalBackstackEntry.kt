package de.gaw.kruiser.backstack.ui.rendering

import androidx.compose.runtime.compositionLocalOf
import de.gaw.kruiser.backstack.core.BackstackEntry

val LocalBackstackEntry = compositionLocalOf<BackstackEntry?> { null }
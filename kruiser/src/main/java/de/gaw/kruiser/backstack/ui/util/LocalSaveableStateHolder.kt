package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.SaveableStateHolder

val LocalSaveableStateHolder =
    compositionLocalOf<SaveableStateHolder> { error("No LocalSaveableStateHolder provided") }
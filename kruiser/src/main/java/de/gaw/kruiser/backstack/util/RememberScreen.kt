package de.gaw.kruiser.backstack.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.destination.Screen

@Composable
fun BackstackEntry.rememberScreen(): Screen = remember(id) {
    destination.build()
}
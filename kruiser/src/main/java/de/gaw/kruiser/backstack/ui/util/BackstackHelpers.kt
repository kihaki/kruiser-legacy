package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.gaw.kruiser.backstack.Backstack

@Composable
fun Backstack.collectEntries() = entries.collectAsState()
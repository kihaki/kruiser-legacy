package de.gaw.kruiser.backstack.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackState
import de.gaw.kruiser.backstack.util.rememberDerivedBackstack

@Composable
fun BackstackState.collectEntries() = entries.collectAsState()

@Composable
fun BackstackState.collectDerivedEntries(
    mapping: BackstackEntries.() -> BackstackEntries,
) = rememberDerivedBackstack(backstack = this, mapping = mapping).collectEntries()
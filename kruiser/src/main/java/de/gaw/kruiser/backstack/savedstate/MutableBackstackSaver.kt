package de.gaw.kruiser.backstack.savedstate

import androidx.compose.runtime.saveable.Saver
import de.gaw.kruiser.backstack.core.MutableBackstack

fun mutableBackstackSaver(): Saver<MutableBackstack, ParceledBackstack> =
    MutableBackstackSaver

private val MutableBackstackSaver = Saver<MutableBackstack, ParceledBackstack>(
    save = { original -> original.asSaveable() },
    restore = { saved -> saved.asMutableBackstack() }
)

internal fun MutableBackstack.asSaveable() = ParceledBackstack(
    id = id,
    entries = entries.value.toList(),
)

internal fun ParceledBackstack.asMutableBackstack() = MutableBackstack(
    id = id,
    initial = entries,
)

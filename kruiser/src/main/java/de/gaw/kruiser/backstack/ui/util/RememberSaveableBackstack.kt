package de.gaw.kruiser.backstack.ui.util

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import de.gaw.kruiser.backstack.BackstackEntries
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.MutableBackstack
import de.gaw.kruiser.destination.Destination
import kotlinx.parcelize.Parcelize

@Composable
@JvmName("rememberSaveableBackstackFromDestinations")
fun rememberSaveableBackstack(initial: List<Destination>): MutableBackstack =
    rememberSaveableBackstack(initial.map { BackstackEntry(it) })

@Composable
fun rememberSaveableBackstack(initial: BackstackEntries = emptyList()): MutableBackstack =
    rememberSaveable(
        initial,
        saver = mutableBackstackSaver(),
    ) {
        MutableBackstack(initial = initial)
    }

@Composable
@JvmName("rememberSaveableBackstackFromDestination")
fun rememberSaveableBackstack(initial: Destination): MutableBackstack =
    rememberSaveableBackstack(listOf(BackstackEntry(initial)))

private fun mutableBackstackSaver(): Saver<MutableBackstack, SaveableBackstackBundle> =
    MutableBackstackSaver

private val MutableBackstackSaver = Saver<MutableBackstack, SaveableBackstackBundle>(
    save = { original ->
        with(original) {
            SaveableBackstackBundle(
                id = id,
                entries = entries.value.toList(),
            )
        }
    },
    restore = { saved ->
        with(saved) {
            MutableBackstack(
                id = id,
                initial = entries,
            )
        }
    }
)

@Parcelize
private data class SaveableBackstackBundle(
    val id: String,
    val entries: BackstackEntries,
) : Parcelable
package de.gaw.kruiser.backstack.ui.rendering

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import de.gaw.kruiser.backstack.core.Backstack
import de.gaw.kruiser.backstack.core.BackstackEntries
import de.gaw.kruiser.backstack.core.BackstackEntry
import de.gaw.kruiser.backstack.core.generateId
import de.gaw.kruiser.backstack.results.BackstackResultsStore
import de.gaw.kruiser.backstack.results.BackstackResultsStoreImpl
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState.EntryTransitionDone
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState.ExitTransitionDone
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionState.ExitTransitionRunning
import de.gaw.kruiser.backstack.ui.transition.ScreenTransitionTracker
import de.gaw.kruiser.backstack.ui.transition.transitionState
import de.gaw.kruiser.backstack.ui.transparency.Transparent
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val LocalOnScreenBackstack = compositionLocalOf<OnScreenBackstack?> { null }

/**
 * Backstack that reflects the state of [BackstackEntry]s that are visible on screen.
 * Also stores the [ScreenTransitionState] of [BackstackEntry]s that are on the [Backstack] and
 * manages transparency.
 */
interface OnScreenBackstack :
    Backstack,
    ScreenTransitionTracker,
    BackstackResultsStore

class DefaultOnScreenBackstack(
    scope: CoroutineScope,
    current: Backstack,
    override val id: String = "${current.id}::${Backstack.generateId()}",
) : OnScreenBackstack,
    BackstackResultsStore by BackstackResultsStoreImpl() {

    private var previousEntries = current.entries.value
    private val exiting = MutableStateFlow<BackstackEntries>(emptyList())
    private val transparent
        get() = (previousEntries + entries.value)
            .toSet()
            .filter { it.destination is Transparent }

    override val transitionStates = MutableStateFlow(
        current.entries.value.associateWith { EntryTransitionDone }
    )

    override val entries: StateFlow<BackstackEntries> =
        combine(current.entries, exiting, transitionStates) { entries, exiting, transitions ->
            val lastVisible = entries.lastOrNull { transitions[it] == EntryTransitionDone }

            val visible = if (lastVisible == null) {
                emptyList()
            } else {
                var previousEntry = lastVisible
                entries
                    .dropLastWhile { it != lastVisible } // Now we have all entries that are not animating in
                    .takeLastWhile { currentEntry ->
                        (currentEntry == lastVisible || transparent.contains(previousEntry))
                            .also { previousEntry = currentEntry }
                    }
            }

            val transitioning = entries.takeLastWhile { transitions[it] != EntryTransitionDone }
            val renderEntries = (visible + transitioning).toPersistentList()
            when (exiting.isEmpty()) {
                true -> renderEntries
                false -> (renderEntries + exiting).toPersistentList()
            }
        }.stateIn(scope, Eagerly, current.entries.value)

    init {
        scope.launch {
            combine(current.entries, transitionStates) { entries, transitions ->
                entries to transitions
            }.collectLatest { (entries, transitions) ->
                // Check if exiting entries need to be adjusted
                exiting.update { cur ->
                    val didShrink = entries.size < previousEntries.size
                    val exiting = when {
                        didShrink -> cur + getOutanimatingEntries(
                            entries = entries,
                            previousEntries = previousEntries,
                            transparentEntries = transparent,
                        )

                        else -> cur
                    }.filterNot { entry ->
                        transitions[entry].let { transition ->
                            transition == null || transition == ExitTransitionDone
                        }
                    }

                    // Remove stale transitions and leftovers
                    transitionStates.update { states ->
                        val updatedTransitionStates = states.filter { (entry, _) ->
                            exiting.contains(entry) || entries.contains(entry)
                        }

                        previousEntries = entries

                        updatedTransitionStates
                    }

                    exiting
                }
            }
        }
    }

    override fun updateTransitionState(
        entry: BackstackEntry,
        transitionState: ScreenTransitionState,
    ) {
        transitionStates.update {
            (it + (entry to transitionState))
                .filterNot { (_, state) ->
                    state == ExitTransitionDone
                }
        }
    }

    override fun onDisposeFromComposition(entry: BackstackEntry) {
        when (transitionState(entry)) {
            ExitTransitionRunning,
            ExitTransitionDone,
            -> updateTransitionState(entry, ExitTransitionDone)

            else -> Unit
        }
    }

    private fun getOutanimatingEntries(
        entries: BackstackEntries,
        previousEntries: BackstackEntries,
        transparentEntries: BackstackEntries,
    ): List<BackstackEntry> {
        return if (previousEntries.lastOrNull()?.let { transparentEntries.contains(it) } == true) {
            // TODO: Check if we are not rendering one too many
            val outCandidates = previousEntries
                .filterNot { it in entries }
                .takeLast(previousEntries.size - entries.size)
            val toFilter = outCandidates
                .dropLastWhile { transparentEntries.contains(it) } // all transparent
                .dropLast(1) // the one below the transparent
                .toSet()
            (outCandidates - toFilter)
        } else {
            listOfNotNull(previousEntries.lastOrNull())
        }
    }
}

@Composable
fun rememberOnScreenBackstack(
    backstack: Backstack = LocalBackstack.currentOrThrow,
): OnScreenBackstack {
    val scope = rememberCoroutineScope()
    return remember(backstack.id) {
        DefaultOnScreenBackstack(
            scope = scope,
            current = backstack,
        )
    }
}


package de.gaw.kruiser.backstack.ui.transition.orchestrator

import android.util.Log
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterExitState.Visible
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import de.gaw.kruiser.backstack.Backstack
import de.gaw.kruiser.backstack.Backstack.Companion.generateId
import de.gaw.kruiser.backstack.BackstackEntries
import de.gaw.kruiser.backstack.BackstackEntry
import de.gaw.kruiser.backstack.ui.transition.orchestrator.ScreenTransitionState.EntryTransitionDone
import de.gaw.kruiser.backstack.ui.transition.orchestrator.ScreenTransitionState.ExitTransitionDone
import de.gaw.kruiser.backstack.ui.transition.orchestrator.ScreenTransitionState.ExitTransitionRunning
import de.gaw.kruiser.backstack.ui.util.LocalBackstack
import de.gaw.kruiser.backstack.ui.util.currentOrThrow
import de.gaw.kruiser.destination.Screen
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

val LocalScreenTransitionBackstack = compositionLocalOf<ScreenTransitionBackstack?> { null }

/**
 * Backstack that reflects the state of [BackstackEntry]s that are visible on screen.
 * Also stores the [ScreenTransitionState] of [BackstackEntry]s that are on the [Backstack],
 */
interface ScreenTransitionBackstack :
    Backstack,
    ScreenTransitionTracker

class DefaultScreenTransitionBackstack(
    scope: CoroutineScope,
    current: Backstack,
    override val id: String = "${generateId()} derived from [${current.id.takeLast(5)}]",
) : ScreenTransitionBackstack {
    private var previousEntries = current.entries.value

    private val exiting = MutableStateFlow<BackstackEntries>(emptyList())
    override val transitionStates = MutableStateFlow(
        current.entries.value.associateWith { EntryTransitionDone }
    )

    override val entries: StateFlow<BackstackEntries> =
        combine(current.entries, exiting, transitionStates) { entries, exiting, transitions ->
            val lastVisible = entries.lastOrNull { transitions[it] == EntryTransitionDone }
            val transitioning = entries.takeLastWhile { transitions[it] != EntryTransitionDone }
            val renderEntries =
                (listOf(lastVisible) + transitioning).filterNotNull().toPersistentList()
            when (exiting.isEmpty()) {
                true -> renderEntries
                false -> (renderEntries + exiting).toPersistentList()
            }
        }.stateIn(scope, Eagerly, current.entries.value)

    override fun initialVisibility(entry: BackstackEntry): Boolean =
        transitionState(entry) == EntryTransitionDone

    init {
        // Cache the topmost removed entry to show it while it is running exit animations
        scope.launch {
            combine(current.entries, transitionStates) { entries, transitions ->
                entries to transitions
            }.collectLatest { (entries, transitions) ->
                // Update cached exiting entry
                exiting.update { cur ->
                    val didShrink = entries.size < previousEntries.size
                    val exiting = when {
                        didShrink -> (cur + previousEntries.lastOrNull()).filterNotNull()
                        else -> cur
                    }.filterNot { entry ->
                        transitions[entry].let { transition ->
                            transition == null || transition == ExitTransitionDone
                        }
                    }

                    Log.v("ScreenTransitioning", "Exiting Entries: ${exiting.size}")

                    // Remove junk
                    transitionStates.update { states ->
                        val updatedTransitionStates = states.filter { (entry, _) ->
                            exiting.contains(entry) || entries.contains(entry)
                        }

                        // Update previous entries
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
}

interface ScreenTransitionTracker {
    /**
     * If the screen transition should start out as visible or invisible
     */
    fun initialVisibility(entry: BackstackEntry): Boolean

    val transitionStates: StateFlow<Map<BackstackEntry, ScreenTransitionState>>
    fun updateTransitionState(entry: BackstackEntry, transitionState: ScreenTransitionState)
    fun onDisposeFromComposition(entry: BackstackEntry)
}

@Composable
fun rememberScreenTransitionsBackstack(
    backstack: Backstack = LocalBackstack.currentOrThrow,
): ScreenTransitionBackstack {
    val scope = rememberCoroutineScope()
    return remember(backstack.id) {
        Log.v(
            "VisibleThing",
            "ScreenTransition Backstack created for ${backstack.id.take(5)}"
        )
        DefaultScreenTransitionBackstack(
            scope = scope,
            current = backstack,
        )
    }
}

/**
 * Describes the state of a [BackstackRenderer]s [Screen] transition
 */
enum class ScreenTransitionState {
    EnterTransitionRunning,
    EntryTransitionDone,
    ExitTransitionRunning,
    ExitTransitionDone;

    companion object {
        @OptIn(ExperimentalAnimationApi::class)
        fun fromTransition(currentState: EnterExitState, targetState: EnterExitState) = when {
            currentState != targetState -> when (targetState) {
                Visible -> EnterTransitionRunning
                else -> ExitTransitionRunning
            }

            else -> when (targetState) {
                Visible -> EntryTransitionDone
                else -> ExitTransitionDone
            }
        }
    }
}

fun ScreenTransitionTracker.transitionState(entry: BackstackEntry) = transitionStates.value[entry]
package de.gaw.kruiser.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import de.gaw.kruiser.destination.Destination

fun NavigationState.push(destination: Destination) = mutate { this + destination }
fun NavigationState.pop() = mutate { dropLast(1) }
fun NavigationState.popAll() = mutate { emptyList() }

val NavigationState.currentStack get() = stack.value

@Composable
fun NavigationState.collectCurrentStack(): State<List<Destination>> =
    stack.collectAsState()

@Composable
fun NavigationState.collectIsEmpty(): State<Boolean> {
    val stack by collectCurrentStack()
    return remember(this.stack) { derivedStateOf { stack.isEmpty() } }
}

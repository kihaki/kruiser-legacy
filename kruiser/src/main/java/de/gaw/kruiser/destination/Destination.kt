package de.gaw.kruiser.destination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import de.gaw.kruiser.screen.Screen
import java.io.Serializable

interface Destination : Serializable {
    fun build(): Screen
}

@Composable
fun Destination.Render() {
    key(this) {
        val screen = remember(this) { build() }
        screen.Content()
    }
}
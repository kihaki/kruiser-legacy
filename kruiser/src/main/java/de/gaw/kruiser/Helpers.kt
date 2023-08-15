package de.gaw.kruiser

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import de.gaw.kruiser.destination.Destination


internal fun Offset.toIntOffset() = IntOffset(x.toInt(), y.toInt())

fun List<Destination>.previousDestination() = dropLast(1).lastOrNull()
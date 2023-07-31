package de.gaw.kruiser.sample.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color

fun colorSaver(): Saver<Color, Long> = Saver(
    save = { it.value.toLong() },

    // Do not use Color(it) directly here, with the Jetpack Overload that takes Long it
    // will not restore as required, because the Color constructor will do numbers magic.
    // We don't want that since we will restore a previously saved, already magicked value.
    // If you do use the wrong overload, the restored color will be transparent!
    restore = { Color(it.toULong()) },
)
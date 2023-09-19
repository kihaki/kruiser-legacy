package de.gaw.kruiser.ui.singletopstack

import de.gaw.kruiser.screen.Screen

/**
 * Screens that are stacked on top of one another require an associated zIndex to be rendered correctly z-depth wise.
 */
data class AnimatedSingleTopStackScreen(
    val zIndex: Float,
    val screen: Screen,
)
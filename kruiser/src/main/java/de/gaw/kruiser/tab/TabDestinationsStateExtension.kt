package de.gaw.kruiser.tab

import de.gaw.kruiser.backstack.core.BackstackEntry

fun TabDestinationsState.setCurrent(entry: BackstackEntry) = setCurrent { _, _ -> entry }
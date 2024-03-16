package de.gaw.kruiser.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Remembers the current [ComponentActivity] that the [LocalContext] is attached to.
 */
@Composable
fun rememberCurrentActivity(): ComponentActivity? {
    val context = LocalContext.current
    return remember(context) { context.findActivity() }
}

/**
 * Recursively finds the [ComponentActivity] that the [Context] is attached to if there is any.
 */
fun Context?.findActivity(): ComponentActivity? = when {
    this is ContextWrapper -> this as? ComponentActivity
        ?: baseContext.findActivity()

    else -> null
}
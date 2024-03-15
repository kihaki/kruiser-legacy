package de.gaw.kruiser.backstack.savedstate

import android.os.Parcelable
import de.gaw.kruiser.backstack.core.BackstackEntries
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableBackstackState(
    val id: String,
    val entries: BackstackEntries,
) : Parcelable
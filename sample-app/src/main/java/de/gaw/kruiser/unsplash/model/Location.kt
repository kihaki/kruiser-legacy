package de.gaw.kruiser.unsplash.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Location(
    @SerialName("city")
    val city: String?,
    @SerialName("country")
    val country: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("position")
    val position: Position?
)
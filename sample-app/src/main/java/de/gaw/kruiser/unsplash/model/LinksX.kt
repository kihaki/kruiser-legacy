package de.gaw.kruiser.unsplash.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class LinksX(
    @SerialName("html")
    val html: String?,
    @SerialName("likes")
    val likes: String?,
    @SerialName("photos")
    val photos: String?,
    @SerialName("portfolio")
    val portfolio: String?,
    @SerialName("self")
    val self: String?
)
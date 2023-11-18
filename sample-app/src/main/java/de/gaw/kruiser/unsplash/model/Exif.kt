package de.gaw.kruiser.unsplash.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Exif(
    @SerialName("aperture")
    val aperture: String?,
    @SerialName("exposure_time")
    val exposureTime: String?,
    @SerialName("focal_length")
    val focalLength: String?,
    @SerialName("iso")
    val iso: Int?,
    @SerialName("make")
    val make: String?,
    @SerialName("model")
    val model: String?
)
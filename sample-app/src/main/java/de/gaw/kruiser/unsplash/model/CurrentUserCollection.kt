package de.gaw.kruiser.unsplash.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class CurrentUserCollection(
    @SerialName("id")
    val id: Int?,
    @SerialName("last_collected_at")
    val lastCollectedAt: String?,
    @SerialName("published_at")
    val publishedAt: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
)
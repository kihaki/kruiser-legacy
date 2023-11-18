package de.gaw.kruiser.unsplash.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class UnsplashPhoto(
    @SerialName("blur_hash")
    val blurHash: String?,
    @SerialName("color")
    val color: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("current_user_collections")
    val currentUserCollections: List<CurrentUserCollection>?,
    @SerialName("description")
    val description: String?,
    @SerialName("downloads")
    val downloads: Int?,
    @SerialName("exif")
    val exif: Exif?,
    @SerialName("height")
    val height: Int?,
    @SerialName("id")
    val id: String?,
    @SerialName("liked_by_user")
    val likedByUser: Boolean?,
    @SerialName("likes")
    val likes: Int?,
    @SerialName("links")
    val links: Links?,
    @SerialName("location")
    val location: Location?,
    @SerialName("updated_at")
    val updatedAt: String?,
    @SerialName("urls")
    val urls: Urls?,
    @SerialName("user")
    val user: User?,
    @SerialName("width")
    val width: Int?
)
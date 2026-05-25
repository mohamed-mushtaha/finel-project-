package com.hotel.hotelbooking.data.model

data class Room(
    val id: String = "",
    val propertyId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val gallery: List<String> = emptyList(),
    val description: String = "",
    val pricePerNight: Double = 0.0,
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val capacity: Int = 2,
    val amenities: List<String> = emptyList(),
    val available: Boolean = true,
    val bookingCount: Int = 0,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "propertyId" to propertyId,
        "name" to name,
        "imageUrl" to imageUrl,
        "gallery" to gallery,
        "description" to description,
        "pricePerNight" to pricePerNight,
        "rating" to rating,
        "ratingCount" to ratingCount,
        "capacity" to capacity,
        "amenities" to amenities,
        "available" to available,
        "bookingCount" to bookingCount,
        "createdAt" to createdAt
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Room = Room(
            id = map["id"] as? String ?: "",
            propertyId = map["propertyId"] as? String ?: "",
            name = map["name"] as? String ?: "",
            imageUrl = map["imageUrl"] as? String ?: "",
            gallery = (map["gallery"] as? List<String>) ?: emptyList(),
            description = map["description"] as? String ?: "",
            pricePerNight = (map["pricePerNight"] as? Number)?.toDouble() ?: 0.0,
            rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
            ratingCount = (map["ratingCount"] as? Number)?.toInt() ?: 0,
            capacity = (map["capacity"] as? Number)?.toInt() ?: 2,
            amenities = (map["amenities"] as? List<String>) ?: emptyList(),
            available = (map["available"] as? Boolean) ?: true,
            bookingCount = (map["bookingCount"] as? Number)?.toInt() ?: 0,
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
        )
    }
}

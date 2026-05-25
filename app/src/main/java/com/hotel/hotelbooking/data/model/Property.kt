package com.hotel.hotelbooking.data.model

enum class PropertyType { HOTEL, APARTMENT;
    companion object {
        fun from(raw: String?): PropertyType = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: HOTEL
    }
}

data class GeoPoint(val latitude: Double = 0.0, val longitude: Double = 0.0)

data class Property(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val gallery: List<String> = emptyList(),
    val rating: Double = 0.0,
    val ratingCount: Int = 0,
    val address: String = "",
    val location: GeoPoint = GeoPoint(),
    val type: PropertyType = PropertyType.HOTEL,
    val description: String = "",
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "ownerId" to ownerId,
        "name" to name,
        "imageUrl" to imageUrl,
        "gallery" to gallery,
        "rating" to rating,
        "ratingCount" to ratingCount,
        "address" to address,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "type" to type.name,
        "description" to description,
        "createdAt" to createdAt
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Property = Property(
            id = map["id"] as? String ?: "",
            ownerId = map["ownerId"] as? String ?: "",
            name = map["name"] as? String ?: "",
            imageUrl = map["imageUrl"] as? String ?: "",
            gallery = (map["gallery"] as? List<String>) ?: emptyList(),
            rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
            ratingCount = (map["ratingCount"] as? Number)?.toInt() ?: 0,
            address = map["address"] as? String ?: "",
            location = GeoPoint(
                latitude = (map["latitude"] as? Number)?.toDouble() ?: 0.0,
                longitude = (map["longitude"] as? Number)?.toDouble() ?: 0.0
            ),
            type = PropertyType.from(map["type"] as? String),
            description = map["description"] as? String ?: "",
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
        )
    }
}

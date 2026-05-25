package com.hotel.hotelbooking.data.model

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED;
    companion object {
        fun from(raw: String?): BookingStatus = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: PENDING
    }
}

data class Booking(
    val id: String = "",
    val userId: String = "",
    val propertyId: String = "",
    val propertyName: String = "",
    val roomId: String = "",
    val roomName: String = "",
    val roomImageUrl: String = "",
    val checkIn: Long = 0L,
    val checkOut: Long = 0L,
    val guests: Int = 1,
    val totalPrice: Double = 0.0,
    val status: BookingStatus = BookingStatus.PENDING,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "userId" to userId,
        "propertyId" to propertyId,
        "propertyName" to propertyName,
        "roomId" to roomId,
        "roomName" to roomName,
        "roomImageUrl" to roomImageUrl,
        "checkIn" to checkIn,
        "checkOut" to checkOut,
        "guests" to guests,
        "totalPrice" to totalPrice,
        "status" to status.name,
        "createdAt" to createdAt
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Booking = Booking(
            id = map["id"] as? String ?: "",
            userId = map["userId"] as? String ?: "",
            propertyId = map["propertyId"] as? String ?: "",
            propertyName = map["propertyName"] as? String ?: "",
            roomId = map["roomId"] as? String ?: "",
            roomName = map["roomName"] as? String ?: "",
            roomImageUrl = map["roomImageUrl"] as? String ?: "",
            checkIn = (map["checkIn"] as? Number)?.toLong() ?: 0L,
            checkOut = (map["checkOut"] as? Number)?.toLong() ?: 0L,
            guests = (map["guests"] as? Number)?.toInt() ?: 1,
            totalPrice = (map["totalPrice"] as? Number)?.toDouble() ?: 0.0,
            status = BookingStatus.from(map["status"] as? String),
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
        )
    }
}

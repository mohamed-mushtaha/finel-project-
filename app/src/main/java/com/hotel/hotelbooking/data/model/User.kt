package com.hotel.hotelbooking.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "email" to email,
        "fullName" to fullName,
        "phone" to phone,
        "photoUrl" to photoUrl,
        "role" to role.name,
        "createdAt" to createdAt
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): User = User(
            uid = map["uid"] as? String ?: "",
            email = map["email"] as? String ?: "",
            fullName = map["fullName"] as? String ?: "",
            phone = map["phone"] as? String ?: "",
            photoUrl = map["photoUrl"] as? String ?: "",
            role = UserRole.from(map["role"] as? String),
            createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L
        )
    }
}

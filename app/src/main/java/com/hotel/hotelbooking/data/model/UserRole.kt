package com.hotel.hotelbooking.data.model

enum class UserRole { CUSTOMER, ADMIN;

    companion object {
        fun from(raw: String?): UserRole = entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: CUSTOMER
    }
}

package com.hotel.hotelbooking.domain.repository

import com.hotel.hotelbooking.data.model.Booking
import com.hotel.hotelbooking.data.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun observeByUser(userId: String): Flow<List<Booking>>
    fun observeByProperty(propertyId: String): Flow<List<Booking>>
    fun observeAll(): Flow<List<Booking>>
    suspend fun create(booking: Booking): Result<Booking>
    suspend fun updateStatus(bookingId: String, status: BookingStatus): Result<Unit>
    suspend fun cancel(bookingId: String): Result<Unit>
}

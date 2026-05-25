package com.hotel.hotelbooking.domain.repository

import android.net.Uri
import com.hotel.hotelbooking.data.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun observeByProperty(propertyId: String): Flow<List<Room>>
    fun observeById(id: String): Flow<Room?>
    fun search(query: String, minPrice: Double? = null, maxPrice: Double? = null, minRating: Double? = null): Flow<List<Room>>
    suspend fun create(room: Room, imageUri: Uri?): Result<Room>
    suspend fun update(room: Room, newImageUri: Uri?): Result<Room>
    suspend fun delete(id: String): Result<Unit>
    suspend fun mostRequested(limit: Int = 5): Result<List<Room>>
}

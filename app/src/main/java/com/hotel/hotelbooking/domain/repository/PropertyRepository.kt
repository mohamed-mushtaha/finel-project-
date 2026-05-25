package com.hotel.hotelbooking.domain.repository

import android.net.Uri
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.PropertyType
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun observeAll(type: PropertyType? = null): Flow<List<Property>>
    fun observeById(id: String): Flow<Property?>
    fun observeByOwner(ownerId: String): Flow<List<Property>>
    suspend fun create(property: Property, imageUri: Uri?): Result<Property>
    suspend fun update(property: Property, newImageUri: Uri?): Result<Property>
    suspend fun delete(id: String): Result<Unit>
    suspend fun topRated(limit: Int = 5): Result<List<Property>>
}

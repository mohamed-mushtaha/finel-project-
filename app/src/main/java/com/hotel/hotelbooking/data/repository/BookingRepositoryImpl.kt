package com.hotel.hotelbooking.data.repository

import com.hotel.hotelbooking.data.model.Booking
import com.hotel.hotelbooking.data.model.BookingStatus
import com.hotel.hotelbooking.data.remote.FirestorePaths
import com.hotel.hotelbooking.data.remote.snapshots
import com.hotel.hotelbooking.domain.repository.BookingRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BookingRepository {

    private val col = firestore.collection(FirestorePaths.BOOKINGS)
    private val rooms = firestore.collection(FirestorePaths.ROOMS)

    override fun observeByUser(userId: String): Flow<List<Booking>> =
        col.whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(Booking::fromMap)

    override fun observeByProperty(propertyId: String): Flow<List<Booking>> =
        col.whereEqualTo("propertyId", propertyId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(Booking::fromMap)

    override fun observeAll(): Flow<List<Booking>> =
        col.orderBy("createdAt", Query.Direction.DESCENDING).snapshots(Booking::fromMap)

    override suspend fun create(booking: Booking): Result<Booking> = runCatching {
        val docRef = col.document()
        val saved = booking.copy(
            id = docRef.id,
            status = BookingStatus.CONFIRMED,
            createdAt = System.currentTimeMillis()
        )
        firestore.runBatch { batch ->
            batch.set(docRef, saved.toMap())
            batch.update(rooms.document(saved.roomId), "bookingCount", FieldValue.increment(1))
        }.await()
        saved
    }

    override suspend fun updateStatus(bookingId: String, status: BookingStatus): Result<Unit> = runCatching {
        col.document(bookingId).update("status", status.name).await()
        Unit
    }

    override suspend fun cancel(bookingId: String): Result<Unit> = runCatching {
        col.document(bookingId).update("status", BookingStatus.CANCELLED.name).await()
        Unit
    }
}

package com.hotel.hotelbooking.data.repository

import android.content.Context
import android.net.Uri
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.data.remote.FirestorePaths
import com.hotel.hotelbooking.data.remote.snapshots
import com.hotel.hotelbooking.data.util.ImageHelper
import com.hotel.hotelbooking.domain.repository.RoomRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : RoomRepository {

    private val col = firestore.collection(FirestorePaths.ROOMS)

    override fun observeByProperty(propertyId: String): Flow<List<Room>> =
        col.whereEqualTo("propertyId", propertyId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(Room::fromMap)

    override fun observeById(id: String): Flow<Room?> = callbackFlow {
        val reg = col.document(id).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.data?.let(Room::fromMap))
        }
        awaitClose { reg.remove() }
    }

    override fun search(query: String, minPrice: Double?, maxPrice: Double?, minRating: Double?): Flow<List<Room>> =
        col.snapshots(Room::fromMap).map { list ->
            list.filter { room ->
                (query.isBlank() || room.name.contains(query, ignoreCase = true) ||
                        room.description.contains(query, ignoreCase = true)) &&
                        (minPrice == null || room.pricePerNight >= minPrice) &&
                        (maxPrice == null || room.pricePerNight <= maxPrice) &&
                        (minRating == null || room.rating >= minRating)
            }
        }

    override suspend fun create(room: Room, imageUri: Uri?): Result<Room> = runCatching {
        val docRef = col.document()
        val id = docRef.id
        val imagePath = imageUri?.let { ImageHelper.saveToInternal(context, it, "room_$id") }
            ?: room.imageUrl
        val saved = room.copy(id = id, imageUrl = imagePath, createdAt = System.currentTimeMillis())
        docRef.set(saved.toMap()).await()
        saved
    }

    override suspend fun update(room: Room, newImageUri: Uri?): Result<Room> = runCatching {
        val imagePath = if (newImageUri != null) {
            if (room.imageUrl.isNotBlank()) ImageHelper.delete(room.imageUrl)
            ImageHelper.saveToInternal(context, newImageUri, "room_${room.id}")
        } else {
            room.imageUrl
        }
        val saved = room.copy(imageUrl = imagePath ?: "")
        col.document(room.id).set(saved.toMap()).await()
        saved
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        val snap = col.document(id).get().await()
        snap.data?.let(Room::fromMap)?.imageUrl?.let { ImageHelper.delete(it) }
        col.document(id).delete().await()
        Unit
    }

    override suspend fun mostRequested(limit: Int): Result<List<Room>> = runCatching {
        val snap = col.orderBy("bookingCount", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
        snap.documents.mapNotNull { it.data?.let(Room::fromMap) }
    }
}

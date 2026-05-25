package com.hotel.hotelbooking.data.repository

import android.content.Context
import android.net.Uri
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.data.remote.FirestorePaths
import com.hotel.hotelbooking.data.remote.snapshots
import com.hotel.hotelbooking.data.util.ImageHelper
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : PropertyRepository {

    private val col = firestore.collection(FirestorePaths.PROPERTIES)

    override fun observeAll(type: PropertyType?): Flow<List<Property>> {
        val q: Query = if (type != null) col.whereEqualTo("type", type.name) else col
        return q.orderBy("createdAt", Query.Direction.DESCENDING).snapshots(Property::fromMap)
    }

    override fun observeById(id: String): Flow<Property?> = callbackFlow {
        val reg = col.document(id).addSnapshotListener { snap, err ->
            if (err != null) { close(err); return@addSnapshotListener }
            trySend(snap?.data?.let(Property::fromMap))
        }
        awaitClose { reg.remove() }
    }

    override fun observeByOwner(ownerId: String): Flow<List<Property>> =
        col.whereEqualTo("ownerId", ownerId).snapshots(Property::fromMap)

    override suspend fun create(property: Property, imageUri: Uri?): Result<Property> = runCatching {
        val docRef = col.document()
        val id = docRef.id
        val imagePath = imageUri?.let { ImageHelper.saveToInternal(context, it, "property_$id") }
            ?: property.imageUrl
        val saved = property.copy(id = id, imageUrl = imagePath, createdAt = System.currentTimeMillis())
        docRef.set(saved.toMap()).await()
        saved
    }

    override suspend fun update(property: Property, newImageUri: Uri?): Result<Property> = runCatching {
        val imagePath = if (newImageUri != null) {
            // Delete old local image before saving new one
            if (property.imageUrl.isNotBlank()) ImageHelper.delete(property.imageUrl)
            ImageHelper.saveToInternal(context, newImageUri, "property_${property.id}")
        } else {
            property.imageUrl
        }
        val saved = property.copy(imageUrl = imagePath ?: "")
        col.document(property.id).set(saved.toMap()).await()
        saved
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        // Clean up local image if exists
        val snap = col.document(id).get().await()
        snap.data?.let(Property::fromMap)?.imageUrl?.let { ImageHelper.delete(it) }
        col.document(id).delete().await()
        Unit
    }

    override suspend fun topRated(limit: Int): Result<List<Property>> = runCatching {
        val snap = col.orderBy("rating", Query.Direction.DESCENDING).limit(limit.toLong()).get().await()
        snap.documents.mapNotNull { it.data?.let(Property::fromMap) }
    }
}

package com.hotel.hotelbooking.data.remote

import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun <T> Query.snapshots(transform: (Map<String, Any?>) -> T): Flow<List<T>> = callbackFlow {
    val reg = addSnapshotListener { snap, err ->
        if (err != null) { close(err); return@addSnapshotListener }
        if (snap != null) trySend(snap.documents.mapNotNull { it.data?.let(transform) })
    }
    awaitClose { reg.remove() }
}

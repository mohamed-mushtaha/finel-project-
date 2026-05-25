package com.hotel.hotelbooking.data.repository

import com.hotel.hotelbooking.data.model.User
import com.hotel.hotelbooking.data.model.UserRole
import com.hotel.hotelbooking.data.remote.FirestorePaths
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersCol = firestore.collection(FirestorePaths.USERS)

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            val uid = fa.currentUser?.uid
            if (uid == null) { trySend(null); return@AuthStateListener }
            usersCol.document(uid).get()
                .addOnSuccessListener { doc ->
                    val data = doc.data
                    if (data != null) trySend(User.fromMap(data)) else trySend(null)
                }
                .addOnFailureListener { trySend(null) }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        role: UserRole
    ): Result<User> = runCatching {
        val cred = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = cred.user?.uid ?: error("Missing uid after signup")
        val user = User(
            uid = uid,
            email = email,
            fullName = fullName,
            phone = phone,
            role = role,
            createdAt = System.currentTimeMillis()
        )
        usersCol.document(uid).set(user.toMap()).await()
        user
    }

    override suspend fun signIn(email: String, password: String): Result<User> = runCatching {
        val cred = auth.signInWithEmailAndPassword(email, password).await()
        val uid = cred.user?.uid ?: error("Missing uid after signin")
        val snap = usersCol.document(uid).get().await()
        snap.data?.let(User::fromMap) ?: error("User record missing")
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
        Unit
    }

    override suspend fun updateProfile(fullName: String, phone: String, photoUrl: String?): Result<User> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in")
        val updates = buildMap<String, Any> {
            put("fullName", fullName)
            put("phone", phone)
            if (photoUrl != null) put("photoUrl", photoUrl)
        }
        usersCol.document(uid).update(updates).await()
        val refreshed = usersCol.document(uid).get().await()
        refreshed.data?.let(User::fromMap) ?: error("User record missing")
    }
}

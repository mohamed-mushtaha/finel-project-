package com.hotel.hotelbooking.domain.repository

import com.hotel.hotelbooking.data.model.User
import com.hotel.hotelbooking.data.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signUp(email: String, password: String, fullName: String, phone: String, role: UserRole): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun updateProfile(fullName: String, phone: String, photoUrl: String?): Result<User>
}

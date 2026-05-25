package com.hotel.hotelbooking.di

import com.hotel.hotelbooking.data.repository.AuthRepositoryImpl
import com.hotel.hotelbooking.data.repository.BookingRepositoryImpl
import com.hotel.hotelbooking.data.repository.PropertyRepositoryImpl
import com.hotel.hotelbooking.data.repository.RoomRepositoryImpl
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.hotel.hotelbooking.domain.repository.BookingRepository
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuth(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindProperty(impl: PropertyRepositoryImpl): PropertyRepository
    @Binds @Singleton abstract fun bindRoom(impl: RoomRepositoryImpl): RoomRepository
    @Binds @Singleton abstract fun bindBooking(impl: BookingRepositoryImpl): BookingRepository
}

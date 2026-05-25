package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hotel.hotelbooking.data.model.Booking
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.hotel.hotelbooking.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val bookings: LiveData<Resource<List<Booking>>> =
        authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) flowOf(Resource.Success(emptyList()))
                else bookingRepository.observeByUser(user.uid)
                    .map<List<Booking>, Resource<List<Booking>>> { Resource.Success(it) }
                    .onStart { emit(Resource.Loading) }
                    .catch { emit(Resource.Error(it.message ?: "Error")) }
            }
            .asLiveData()
}

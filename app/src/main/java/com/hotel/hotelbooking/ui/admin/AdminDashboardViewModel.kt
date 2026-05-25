package com.hotel.hotelbooking.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.hotel.hotelbooking.domain.repository.BookingRepository
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val totalProperties: Int = 0,
    val totalBookings: Int = 0,
    val topRatedProperties: List<Property> = emptyList(),
    val mostRequestedRooms: List<Room> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val propertyRepository: PropertyRepository,
    private val roomRepository: RoomRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _state = MutableLiveData(DashboardState())
    val state: LiveData<DashboardState> = _state

    init { loadStats() }

    fun loadStats() {
        _state.value = _state.value!!.copy(loading = true)
        viewModelScope.launch {
            val userId = authRepository.currentUser.first()?.uid ?: ""
            val properties = propertyRepository.observeByOwner(userId).first()
            val propertyIds = properties.map { it.id }.toSet()
            val allBookings = bookingRepository.observeAll().first()
            val bookings = allBookings.filter { it.propertyId in propertyIds }

            val topRated = propertyRepository.topRated(5).getOrDefault(emptyList())
            val mostRequested = roomRepository.mostRequested(5).getOrDefault(emptyList())

            _state.value = DashboardState(
                totalProperties = properties.size,
                totalBookings = bookings.size,
                topRatedProperties = topRated,
                mostRequestedRooms = mostRequested,
                loading = false
            )
        }
    }
}

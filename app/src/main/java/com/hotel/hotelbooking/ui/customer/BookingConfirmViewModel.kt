package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.Booking
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.domain.repository.BookingRepository
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingConfirmState(
    val room: Room? = null,
    val property: Property? = null,
    val checkIn: Long? = null,
    val checkOut: Long? = null,
    val guests: Int = 1,
    val loading: Boolean = false,
    val bookingSuccess: Boolean = false,
    val error: String? = null
) {
    val nights: Int get() = if (checkIn != null && checkOut != null && checkOut > checkIn)
        ((checkOut - checkIn) / 86_400_000).toInt() else 0

    val totalPrice: Double get() = (room?.pricePerNight ?: 0.0) * nights

    val canConfirm: Boolean get() = checkIn != null && checkOut != null &&
        checkOut > checkIn && nights > 0 && !loading
}

@HiltViewModel
class BookingConfirmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRepository: RoomRepository,
    private val propertyRepository: PropertyRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])
    private val propertyId: String = checkNotNull(savedStateHandle["propertyId"])

    private val _state = MutableLiveData(BookingConfirmState())
    val state: LiveData<BookingConfirmState> = _state

    private var currentUserId: String = ""

    init {
        viewModelScope.launch {
            val room = roomRepository.observeById(roomId).first()
            val property = propertyRepository.observeById(propertyId).first()
            _state.value = _state.value!!.copy(room = room, property = property)
        }
    }

    fun setUserId(uid: String) { currentUserId = uid }

    fun setDates(checkInMs: Long, checkOutMs: Long) {
        _state.value = _state.value!!.copy(checkIn = checkInMs, checkOut = checkOutMs, error = null)
    }

    fun incrementGuests() {
        val current = _state.value!!
        val max = current.room?.capacity ?: 10
        if (current.guests < max) _state.value = current.copy(guests = current.guests + 1)
    }

    fun decrementGuests() {
        val current = _state.value!!
        if (current.guests > 1) _state.value = current.copy(guests = current.guests - 1)
    }

    fun confirmBooking() {
        val s = _state.value!!
        if (!s.canConfirm) {
            _state.value = s.copy(error = "Please select valid check-in and check-out dates.")
            return
        }
        val room = s.room ?: return
        val property = s.property ?: return

        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val booking = Booking(
                userId = currentUserId,
                propertyId = property.id,
                propertyName = property.name,
                roomId = room.id,
                roomName = room.name,
                roomImageUrl = room.imageUrl,
                checkIn = s.checkIn!!,
                checkOut = s.checkOut!!,
                guests = s.guests,
                totalPrice = s.totalPrice
            )
            bookingRepository.create(booking)
                .onSuccess { _state.value = s.copy(loading = false, bookingSuccess = true) }
                .onFailure { _state.value = s.copy(loading = false, error = it.message ?: "Booking failed") }
        }
    }
}

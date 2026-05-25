package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class RoomDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val roomId: String = checkNotNull(savedStateHandle["roomId"])

    val room: LiveData<Resource<Room?>> =
        roomRepository.observeById(roomId)
            .map<Room?, Resource<Room?>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error")) }
            .asLiveData()
}

package com.hotel.hotelbooking.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.hotel.hotelbooking.domain.repository.PropertyRepository
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
class AdminPropertiesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    val properties: LiveData<Resource<List<Property>>> =
        authRepository.currentUser
            .flatMapLatest { user ->
                if (user == null) flowOf(Resource.Success(emptyList()))
                else propertyRepository.observeByOwner(user.uid)
                    .map<List<Property>, Resource<List<Property>>> { Resource.Success(it) }
                    .onStart { emit(Resource.Loading) }
                    .catch { emit(Resource.Error(it.message ?: "Error")) }
            }
            .asLiveData()
}

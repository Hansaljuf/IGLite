package com.example.iglite.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.remote.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val instagramLiteRepository: InstagramLiteRepository
) : ViewModel() {

    private var _registerResult: MutableLiveData<RegisterResponse> = MutableLiveData()
    val registerResult get() = _registerResult

    fun instagramLiteSubmitRegister(name: String, username: String, password: String) {
        viewModelScope.launch {
            _registerResult.value =
                instagramLiteRepository.instagramLiteSubmitRegister(name, username, password)
        }
    }
}
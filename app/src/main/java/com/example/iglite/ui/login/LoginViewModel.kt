package com.example.iglite.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.remote.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val instagramLiteRepository: InstagramLiteRepository
) : ViewModel() {

    private var _loginResult: MutableLiveData<LoginResponse> = MutableLiveData()
    val loginResult get() = _loginResult

    private var _userToken: MutableLiveData<String> = MutableLiveData()
    val userToken get() = _userToken

    fun instagramLiteSubmitLogin(username: String, password: String) {
        viewModelScope.launch {
            _loginResult.value =
                instagramLiteRepository.instagramLiteSubmitLogin(username, password)
        }
    }

    fun getUserToken() {
        viewModelScope.launch {
            instagramLiteRepository.getUserToken().collectLatest {
                _userToken.value = it
            }
        }
    }

    fun saveUserToken(userToken: String) {
        viewModelScope.launch {
            instagramLiteRepository.saveUserToken(userToken)
        }
    }
}
package com.example.iglite.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.local.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val instagramLiteRepository: InstagramLiteRepository
) : ViewModel() {

    private var _storyResult: MutableLiveData<PagingData<Story>> =
        MutableLiveData(PagingData.empty())
    val storyResult: LiveData<PagingData<Story>> get() = _storyResult

    private var _userToken: MutableLiveData<String> = MutableLiveData()
    val userToken get() = _userToken

    fun instagramLiteGetStories(
        token: String, location: Int = 0
    ) {
        _storyResult = instagramLiteRepository.getStoriesLiveData(
            "Bearer $token", location,
        ) as MutableLiveData<PagingData<Story>>
    }

    fun getUserToken() {
        viewModelScope.launch {
            instagramLiteRepository.getUserToken().collectLatest {
                _userToken.value = it
            }
        }
    }

    fun clearUserToken() {
        viewModelScope.launch {
            instagramLiteRepository.clearUserToken()
        }
    }
}
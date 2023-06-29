package com.example.iglite.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.local.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val instagramLiteRepository: InstagramLiteRepository
) : ViewModel() {

    private var _storyResult: MutableLiveData<List<Story>> = MutableLiveData()
    val storyResult: LiveData<List<Story>> = _storyResult

    fun getAllStories() {
        viewModelScope.launch {
            _storyResult.value = instagramLiteRepository.getAllStoriesFromDb()
        }
    }
}
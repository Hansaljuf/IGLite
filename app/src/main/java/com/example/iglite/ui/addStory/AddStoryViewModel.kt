package com.example.iglite.ui.addStory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iglite.data.InstagramLiteRepository
import com.example.iglite.data.remote.response.AddNewStoryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Part
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val instagramLiteRepository: InstagramLiteRepository
) : ViewModel() {

    private var _addNewStoryResult: MutableLiveData<AddNewStoryResponse> = MutableLiveData()
    val addNewStoryResult get() = _addNewStoryResult

    fun instagramLiteAddStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ) {
        viewModelScope.launch {
            _addNewStoryResult.value = instagramLiteRepository.instagramLiteAddStory(
                "Bearer $token", file, description, lat, lon
            )
        }
    }
}
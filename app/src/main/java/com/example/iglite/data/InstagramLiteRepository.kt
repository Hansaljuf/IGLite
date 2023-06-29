package com.example.iglite.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.iglite.data.local.StoriesRemoteMediator
import com.example.iglite.data.local.db.StoriesDatabase
import com.example.iglite.data.local.model.Story
import com.example.iglite.data.local.preference.PreferenceSession
import com.example.iglite.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class InstagramLiteRepository @Inject constructor(
    private val apiService: ApiService,
    private val storiesDatabase: StoriesDatabase,
    private val preferenceSession: PreferenceSession
) {
    suspend fun instagramLiteSubmitRegister(
        name: String, email: String, password: String
    ) = apiService.instagramLiteRegister(name, email, password)

    suspend fun instagramLiteSubmitLogin(
        email: String, password: String
    ) = apiService.instagramLiteLogin(email, password)

    suspend fun instagramLiteAddStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody,
        lon: RequestBody
    ) = apiService.instagramLiteAddStory(token, file, description, lat, lon)

//    suspend fun instagramLiteGetStories(
//        token: String,
//    ) = apiService.instagramLiteGetStories(token)

    fun getUserToken() = preferenceSession.getUserToken()

    suspend fun saveUserToken(userToken: String) {
        preferenceSession.saveUserToken(userToken)
    }

    suspend fun clearUserToken() {
        preferenceSession.clearUserToken()
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesLiveData(token: String, location: Int = 0): LiveData<PagingData<Story>> = Pager(
        config = PagingConfig(pageSize = 10),
        remoteMediator = StoriesRemoteMediator(token, location, apiService, storiesDatabase),
        pagingSourceFactory = {
            storiesDatabase.storiesDao().getAllStories()
        }).liveData

    suspend fun getAllStoriesFromDb() = storiesDatabase.storiesDao().getStoriesFromDb()

}
package com.example.iglite.network

import com.example.iglite.data.remote.response.AddNewStoryResponse
import com.example.iglite.data.remote.response.GetAllStoryResponse
import com.example.iglite.data.remote.response.LoginResponse
import com.example.iglite.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun instagramLiteRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun instagramLiteLogin(
        @Field("email") email: String, @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun instagramLiteAddStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): AddNewStoryResponse

    @GET("stories")
    suspend fun instagramLiteGetStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): GetAllStoryResponse
}
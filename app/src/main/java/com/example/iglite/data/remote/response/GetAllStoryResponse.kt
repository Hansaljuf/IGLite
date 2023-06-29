package com.example.iglite.data.remote.response

import com.example.iglite.data.local.model.Story
import com.google.gson.annotations.SerializedName

data class GetAllStoryResponse(

    @field:SerializedName("listStory") val listStory: List<Story?>? = null,

    @field:SerializedName("error") val error: Boolean? = null,

    @field:SerializedName("message") val message: String? = null
)
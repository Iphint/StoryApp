package com.arifin.newest.data.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

package com.arifin.newest.data.retrofit

import com.arifin.newest.data.response.GetAllStoriesResponse
import com.arifin.newest.data.response.ListStoryItem
import com.arifin.newest.data.response.LocationResponse
import com.arifin.newest.data.response.LoginResponse
import com.arifin.newest.data.response.RegisterResponse
import com.arifin.newest.data.response.TambahResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiServices {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): GetAllStoriesResponse

    @GET("stories?location=1")
    fun getLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
    ): Call<LocationResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<TambahResponse>
}

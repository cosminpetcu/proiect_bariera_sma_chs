package com.example.smaproject_1

import retrofit2.Response
import retrofit2.http.*

data class AccessPoint(
    val pointId: String,
    val userId: String,
    val name: String,
    val code: String,
    val address: String
)

data class AccessPointResponse(
    val success: Boolean,
    val message: String
)

interface ApiService {
    @POST("/api/accessPoints/add")
    suspend fun addAccessPoint(@Body accessPoint: AccessPoint): Response<AccessPointResponse>

    @GET("/api/accessPoints/user/{userId}")
    fun getAccessPoints(@Path("userId") userId: String): Response<List<AccessPoint>>

    @DELETE("/api/accessPoints/delete/{pointId}")
    suspend fun deleteAccessPoint(@Path("pointId") pointId: String): Response<Unit>
}


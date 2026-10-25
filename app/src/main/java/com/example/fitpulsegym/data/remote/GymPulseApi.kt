package com.example.fitpulsegym.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Backend contract. Point BASE_URL to your Node/Express or Firebase Functions endpoint.
 * Repository falls back to Room cache when the network fails (offline-first).
 */
interface GymPulseApi {
  @GET("classes")
  suspend fun getSchedule(@Query("day") dayIndex: Int? = null): List<GymClassDto>

  @POST("bookings")
  suspend fun bookSlot(@Body req: BookSlotRequest): BookingDto

  @POST("bookings/{id}/cancel")
  suspend fun cancelBooking(@Path("id") bookingId: String)

  @GET("memberships/{userId}")
  suspend fun getMembership(@Path("userId") userId: String): MembershipDto

  companion object {
    const val BASE_URL = "https://api.gympulse.example/v1/"
  }
}

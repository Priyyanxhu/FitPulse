package com.example.fitpulsegym.di

import com.example.fitpulsegym.data.remote.GymPulseApi
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides @Singleton
  fun provideJson(): Json = Json { ignoreUnknownKeys = true; explicitNulls = false }

  @Provides @Singleton
  fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    .build()

  @Provides @Singleton
  fun provideApi(json: Json, client: OkHttpClient): GymPulseApi {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
      .baseUrl(GymPulseApi.BASE_URL)
      .client(client)
      .addConverterFactory(json.asConverterFactory(contentType))
      .build()
      .create(GymPulseApi::class.java)
  }
}

package com.skreczmanski.oasisdictionary.network

import com.skreczmanski.oasisdictionary.api.PolAngApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    val apiService: PolAngApiService by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://light-life.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PolAngApiService::class.java)
    }
}
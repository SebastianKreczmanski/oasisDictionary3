package com.skreczmanski.oasisdictionary.api

import com.skreczmanski.oasisdictionary.z_jsona.PolAngItem
import retrofit2.http.GET

interface PolAngApiService {
    @GET("pobierz_tlumaczenia_angielskie.php")
    suspend fun getTranslations(): List<PolAngItem>
}
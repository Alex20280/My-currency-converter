package com.example.currencyconverter.data

import com.example.mycurencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApi {
    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getRates(): Response<CurrencyResponse>
}
package com.example.currencyconverter.data

import com.example.mycurencyconverter.data.model.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("/tasks/api/currency-exchange-rates")
    suspend fun getRates(@Query("base") base: String) : Response<CurrencyResponse>
}
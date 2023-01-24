package com.example.currencyconverter.domain

import com.example.mycurencyconverter.data.model.CurrencyResponse
import com.example.mycurencyconverter.utils.Resource

interface GetRatesUseCase {
    suspend fun getRates(): Resource<CurrencyResponse>
}
package com.example.currencyconverter.domain

import com.example.mycurencyconverter.data.model.CurrencyResponse
import com.example.mycurencyconverter.utils.Resource

interface CurrencyConverter {
    suspend fun getRates (baseUrl: String): Resource<CurrencyResponse>
}
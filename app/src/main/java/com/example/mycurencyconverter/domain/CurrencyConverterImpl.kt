package com.example.mycurencyconverter.domain

import com.example.currencyconverter.data.CurrencyApi
import com.example.currencyconverter.domain.CurrencyConverter
import com.example.mycurencyconverter.data.model.CurrencyResponse
import com.example.mycurencyconverter.utils.Resource
import javax.inject.Inject

class CurrencyConverterImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyConverter  {

    override suspend fun getRates(base: String): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates(base)
            val result = response.body()
            if(response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch(e: Exception) {
            Resource.Error(e.message ?: "An error occured")
        }
    }
}


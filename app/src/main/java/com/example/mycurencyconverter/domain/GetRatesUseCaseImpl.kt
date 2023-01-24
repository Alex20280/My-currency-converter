package com.example.mycurencyconverter.domain

import com.example.currencyconverter.data.CurrencyApi
import com.example.currencyconverter.domain.GetRatesUseCase
import com.example.mycurencyconverter.data.model.CurrencyResponse
import com.example.mycurencyconverter.utils.Resource
import javax.inject.Inject

class GetRatesUseCaseImpl @Inject constructor(
    private val api: CurrencyApi
) : GetRatesUseCase {

    override suspend fun getRates(): Resource<CurrencyResponse> {
        return try {
            val response = api.getRates()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

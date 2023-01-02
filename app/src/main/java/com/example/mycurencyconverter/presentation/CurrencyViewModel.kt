package com.example.mycurencyconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.CurrencyConverter
import com.example.mycurencyconverter.data.model.Rates
import com.example.mycurencyconverter.utils.DispatcherProvider
import com.example.mycurencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    val repository: CurrencyConverter,
    val dispatchers:DispatcherProvider
): ViewModel(){

    private val currentEuroBalance = 1000.0
    private var currentUsdBalance = 0.0
    private val feeCounter = 0
    private val commissionFee = 0.007

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    private val _currentEuroBalance = MutableStateFlow<Double>(1000.0)
    val euroBalance: StateFlow<Double> = _currentEuroBalance

/*    private val _currentEuroBalance: MutableStateFlow <Double> = MutableStateFlow (currentEuroBalance)
    val euroBalance: StateFlow <Double> = _currentEuroBalance*/

    private val _currentUsdBalance: MutableStateFlow <Double> = MutableStateFlow (0.0)
    val usdBalance: StateFlow <Double> = _currentUsdBalance

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Loading : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    fun convert(amountStr: String, fromCurrency: String, toCurrency: String) {
        val fromAmount = amountStr.toFloatOrNull()
        if(fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
/*        else if (currentBalance < 0) {
            _conversion.value = CurrencyEvent.Failure("Balance cannot be negative")
            return
        }*/

        viewModelScope.launch(dispatchers.io) {
            _conversion.value = CurrencyEvent.Loading
            when(val ratesResponse = repository.getRates(fromCurrency)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency(toCurrency, rates)
                    if(rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency = round(fromAmount * rate.toString().toDouble() * 100) / 100
                        if (fromCurrency == "EUR"){
                            _currentEuroBalance.value  = currentEuroBalance - amountStr.toDouble()
                        } else if (toCurrency == "USD"){
                            _currentUsdBalance.value = currentUsdBalance + convertedCurrency
                        }
                        _conversion.value = CurrencyEvent.Success(convertedCurrency.toString())
                    }
                }
            }
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "CAD" -> rates.cAD
        "HKD" -> rates.hKD
        "ISK" -> rates.iSK
        "EUR" -> rates.eUR
        "PHP" -> rates.pHP
        "DKK" -> rates.dKK
        "HUF" -> rates.hUF
        "CZK" -> rates.cZK
        "AUD" -> rates.aUD
        "RON" -> rates.rON
        "SEK" -> rates.sEK
        "IDR" -> rates.iDR
        "INR" -> rates.iNR
        "BRL" -> rates.bRL
        "RUB" -> rates.rUB
        "HRK" -> rates.hRK
        "JPY" -> rates.jPY
        "THB" -> rates.tHB
        "CHF" -> rates.cHF
        "SGD" -> rates.sGD
        "PLN" -> rates.pLN
        "BGN" -> rates.bGN
        "CNY" -> rates.cNY
        "NOK" -> rates.nOK
        "NZD" -> rates.nZD
        "ZAR" -> rates.zAR
        "USD" -> rates.uSD
        "MXN" -> rates.mXN
        "ILS" -> rates.iLS
        "GBP" -> rates.gBP
        "KRW" -> rates.kRW
        "MYR" -> rates.mYR
        else -> null
    }

}
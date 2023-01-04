package com.example.mycurencyconverter.presentation

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.CurrencyConverter
import com.example.mycurencyconverter.data.model.Rates
import com.example.mycurencyconverter.utils.DispatcherProvider
import com.example.mycurencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round
import kotlin.math.roundToInt

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    val repository: CurrencyConverter,
    val dispatchers:DispatcherProvider
): ViewModel(){

    val currentEuroBalance = 1000.0
    private var currentUsdBalance = 0.0
    private var currentBngBalance = 0.0
    private var transactionCounter = 0
    private var freeTransactionLimit = 4
    private val commissionFee = 0.007
    var uiComisionFee = 0.0
    private var convertedAmount = 0.0

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    private val _currentEuroBalance: MutableStateFlow <Double> = MutableStateFlow (currentEuroBalance)
    val euroBalance: StateFlow <Double> = _currentEuroBalance

    private val _currentUsdBalance: MutableStateFlow <Double> = MutableStateFlow (currentUsdBalance)
    val usdBalance: StateFlow <Double> = _currentUsdBalance

    private val _currentBngBalance: MutableStateFlow <Double> = MutableStateFlow (currentBngBalance)
    val bngBalance: StateFlow <Double> = _currentBngBalance

    sealed class CurrencyEvent {
        class Success(val resultText: String): CurrencyEvent()
        class Failure(val errorText: String): CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    fun convert(amountStr: String, fromCurrency: String, toCurrency: String) {
        val fromAmount = amountStr.toFloatOrNull()
        if(fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
        if (_currentEuroBalance.value - amountStr.toDouble() <= 0) {
            return
        }
        viewModelScope.launch(dispatchers.io) {
            when(val ratesResponse = repository.getRates(fromCurrency)) {
                is Resource.Error -> _conversion.value = CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency(toCurrency, rates)
                    if(rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        if (transactionCounter > freeTransactionLimit){
                            convertedAmount = round(fromAmount * rate.toString().toDouble() * 100) / 100
                            _currentEuroBalance.value  -= currentEuroBalance*commissionFee
                            uiComisionFee = 0.70
                        } else{
                            convertedAmount = round(fromAmount * rate.toString().toDouble() * 100) / 100
                        }
                        if (fromCurrency == "EUR"){
                            _currentEuroBalance.value  -= amountStr.toDouble()
                        }
                        if (toCurrency == "USD"){
                            _currentUsdBalance.value  += convertedAmount
                        }
                        if (toCurrency == "BGN"){
                            _currentBngBalance.value  += convertedAmount
                        }
                        if (toCurrency == "EUR"){
                            _currentEuroBalance.value  += convertedAmount
                        }
                        _conversion.value = CurrencyEvent.Success(convertedAmount.toString())
                        transactionCounter++
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
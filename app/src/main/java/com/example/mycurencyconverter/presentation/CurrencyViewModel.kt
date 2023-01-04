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
    val dispatchers: DispatcherProvider
) : ViewModel() {

    private var currentEuroBalance = 1000.0
    private var currentUsdBalance = 0.0
    private var currentBngBalance = 0.0
    private var currentOtherBalance = 0.0
    private var transactionCounter = 0
    private var freeTransactionLimit = 5
    private val commissionFee = 0.07
    private var convertedAmount = 0.0

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    private val _currentEuroBalance: MutableStateFlow<Double> = MutableStateFlow(currentEuroBalance)
    val euroBalance: StateFlow<Double> = _currentEuroBalance

    private val _currentUsdBalance: MutableStateFlow<Double> = MutableStateFlow(currentUsdBalance)
    val usdBalance: StateFlow<Double> = _currentUsdBalance

    private val _currentBngBalance: MutableStateFlow<Double> = MutableStateFlow(currentBngBalance)
    val bngBalance: StateFlow<Double> = _currentBngBalance

    private val _currentOtherCurrenciesBalance: MutableStateFlow<Double> = MutableStateFlow(currentOtherBalance)
    val otherBalance: StateFlow<Double> = _currentOtherCurrenciesBalance

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Empty : CurrencyEvent()
    }


    fun convert(amountStr: String, fromCurrency: String, toCurrency: String, callback:(fee:Double) -> Unit ) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
        val currentBalance = getBalance(fromCurrency).value
        val fee  =  if (transactionCounter >= freeTransactionLimit) {
            fromAmount*commissionFee
        } else 0.0

        if(currentBalance<fromAmount + fee){
            return
        }

        viewModelScope.launch(dispatchers.io) {
            when (val ratesResponse = repository.getRates(fromCurrency)) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency(toCurrency, rates)
                    if (rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        convertedAmount =  round(fromAmount * rate.toString().toDouble() * 100) / 100

                        updateBalance(toCurrency, fromCurrency, convertedAmount, fromAmount, fee)

                        _conversion.value = CurrencyEvent.Success(convertedAmount.toString())
                        callback.invoke(commissionFee)
                        transactionCounter++
                    }
                }
            }
        }
    }

    private fun getBalance(fromCurrency: String) =
        when (fromCurrency) {
            "EUR" -> _currentEuroBalance
            "USD" -> _currentUsdBalance
            "BNG" -> _currentBngBalance
            else -> _currentOtherCurrenciesBalance
        }


    private fun updateBalance(
        toCurrency: String,
        fromCurrency: String,
        convertedAmount: Double,
        fromAmount: Float,
        fee: Double
    ) {
        val balanceFromFromStateFlow = getBalance(fromCurrency)
        balanceFromFromStateFlow.value -= fromAmount + fee
        val balanceToStateFlow = getBalance(toCurrency)
        balanceToStateFlow.value += convertedAmount
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


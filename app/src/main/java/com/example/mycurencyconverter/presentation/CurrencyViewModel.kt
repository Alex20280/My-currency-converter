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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyConverter,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var currentEuroBalance = 1000.0
    private var currentUsdBalance = 0.0
    private var currentBngBalance = 0.0
    private var currentOtherBalance = 0.0
    private var transactionCounter = 0
    private var freeTransactionLimit = 4
    private val commissionFee = 0.07
    private val zeroCommissionFee = 0.0
    private var convertedAmount = 0.0
    private var rates: Rates? = null

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    private val _currentEuroBalance: MutableStateFlow<Double> = MutableStateFlow(currentEuroBalance)
    val euroBalance: StateFlow<Double> = _currentEuroBalance.asStateFlow()

    private val _currentUsdBalance: MutableStateFlow<Double> = MutableStateFlow(currentUsdBalance)
    val usdBalance: StateFlow<Double> = _currentUsdBalance.asStateFlow()

    private val _currentBngBalance: MutableStateFlow<Double> = MutableStateFlow(currentBngBalance)
    val bngBalance: StateFlow<Double> = _currentBngBalance.asStateFlow()

    private val _currentOtherCurrenciesBalance: MutableStateFlow<Double> = MutableStateFlow(currentOtherBalance)
    val otherBalance: StateFlow<Double> = _currentOtherCurrenciesBalance.asStateFlow()

    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Empty : CurrencyEvent()
    }

    fun startLoader() {
        viewModelScope.launch(dispatchers.io) {
            when (val ratesResponse = repository.getRates()) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(ratesResponse.message!!)
                is Resource.Success -> {
                    rates = ratesResponse.data!!.rates
                }
            }
        }
    }

    fun convert(amountStr: String, fromCurrency: String, toCurrency: String, callback: (fee: Double) -> Unit) {
        val fromAmount = amountStr.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
        val fee = if (transactionCounter >= freeTransactionLimit) {
            fromAmount * commissionFee
        } else {
            zeroCommissionFee
        }
        if (!validateCurrencies(fromCurrency, toCurrency)) return

        if (!validateNegativeBalance(fromCurrency, fromAmount, fee)) return

        val rate = getRateForCurrency(toCurrency, rates)
        convertedAmount = round(fromAmount * rate.toString().toDouble() * 100) / 100
        updateBalance(toCurrency, fromCurrency, convertedAmount, fromAmount, fee)
        _conversion.value = CurrencyEvent.Success(convertedAmount.toString())
        if (transactionCounter >= freeTransactionLimit) {
            callback.invoke(commissionFee)
        }
        transactionCounter++
    }

    private fun validateCurrencies(fromCurrency: String, toCurrency: String): Boolean {
        if (fromCurrency == toCurrency){
            _conversion.value = CurrencyEvent.Failure("Please check currencies")
            return false
        } else {
            return true
        }
    }

    private fun validateNegativeBalance(fromCurrency: String, fromAmount: Float, fee: Double): Boolean {
        val currentBalance = getBalance(fromCurrency).value
        if (currentBalance < fromAmount + fee) {
            _conversion.value = CurrencyEvent.Failure("You cannot go to negative balance")
            return false
        }
        return true
    }

    private fun getBalance(fromCurrency: String) =
        when (fromCurrency) {
            "EUR" -> _currentEuroBalance
            "USD" -> _currentUsdBalance
            "BGN" -> _currentBngBalance
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

    private fun getRateForCurrency(currency: String, rates: Rates?) = when (currency) {
        "CAD" -> rates?.cAD
        "HKD" -> rates?.hKD
        "ISK" -> rates?.iSK
        "EUR" -> rates?.eUR
        "PHP" -> rates?.pHP
        "DKK" -> rates?.dKK
        "HUF" -> rates?.hUF
        "CZK" -> rates?.cZK
        "AUD" -> rates?.aUD
        "RON" -> rates?.rON
        "SEK" -> rates?.sEK
        "IDR" -> rates?.iDR
        "INR" -> rates?.iNR
        "BRL" -> rates?.bRL
        "RUB" -> rates?.rUB
        "HRK" -> rates?.hRK
        "JPY" -> rates?.jPY
        "THB" -> rates?.tHB
        "CHF" -> rates?.cHF
        "SGD" -> rates?.sGD
        "PLN" -> rates?.pLN
        "BGN" -> rates?.bGN
        "CNY" -> rates?.cNY
        "NOK" -> rates?.nOK
        "NZD" -> rates?.nZD
        "ZAR" -> rates?.zAR
        "USD" -> rates?.uSD
        "MXN" -> rates?.mXN
        "ILS" -> rates?.iLS
        "GBP" -> rates?.gBP
        "KRW" -> rates?.kRW
        "MYR" -> rates?.mYR
        else -> null
    }
}
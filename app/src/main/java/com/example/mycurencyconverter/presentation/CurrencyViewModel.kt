package com.example.mycurencyconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.GetRatesUseCase
import com.example.mycurencyconverter.data.model.Rates
import com.example.mycurencyconverter.di.IoDispatcher
//import com.example.mycurencyconverter.utils.DispatcherProvider
import com.example.mycurencyconverter.utils.Resource
import com.example.mycurencyconverter.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: GetRatesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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

    private val mConversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = mConversion

    private val mCurrentEuroBalance: MutableStateFlow<Double> = MutableStateFlow(currentEuroBalance)
    val euroBalance: StateFlow<Double> = mCurrentEuroBalance.asStateFlow()

    private val mCurrentUsdBalance: MutableStateFlow<Double> = MutableStateFlow(currentUsdBalance)
    val usdBalance: StateFlow<Double> = mCurrentUsdBalance.asStateFlow()

    private val mCurrentBngBalance: MutableStateFlow<Double> = MutableStateFlow(currentBngBalance)
    val bngBalance: StateFlow<Double> = mCurrentBngBalance.asStateFlow()

    private val mCurrentOtherCurrenciesBalance: MutableStateFlow<Double> = MutableStateFlow(currentOtherBalance)
    val otherBalance: StateFlow<Double> = mCurrentOtherCurrenciesBalance.asStateFlow()

    fun startLoader() {
        viewModelScope.launch(ioDispatcher) {
            when (val ratesResponse = repository.getRates()) {
                is Resource.Error -> mConversion.value =
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
            mConversion.value = CurrencyEvent.Failure("Not a valid amount")
            return
        }
        val fee = if (transactionCounter >= freeTransactionLimit) {
            fromAmount * commissionFee
        } else {
            zeroCommissionFee
        }
        if (!validateCurrencies(fromCurrency, toCurrency)) return

        if (!validateNegativeBalance(fromCurrency, fromAmount, fee)) return

        val rate = Utils.getRateForCurrency(toCurrency, rates)
        convertedAmount = round(fromAmount * rate.toString().toDouble() * 100) / 100
        updateBalance(toCurrency, fromCurrency, convertedAmount, fromAmount, fee)
        mConversion.value = CurrencyEvent.Success(convertedAmount.toString())
        if (transactionCounter >= freeTransactionLimit) {
            callback.invoke(commissionFee)
        }
        transactionCounter++
    }

    private fun validateCurrencies(fromCurrency: String, toCurrency: String): Boolean {
        if (fromCurrency == toCurrency){
            mConversion.value = CurrencyEvent.Failure("Please check currencies")
            return false
        } else {
            return true
        }
    }

    private fun validateNegativeBalance(fromCurrency: String, fromAmount: Float, fee: Double): Boolean {
        val currentBalance = getBalance(fromCurrency).value
        if (currentBalance < fromAmount + fee) {
            mConversion.value = CurrencyEvent.Failure("You cannot go to negative balance")
            return false
        }
        return true
    }

    private fun getBalance(fromCurrency: String) =
        when (fromCurrency) {
            "EUR" -> mCurrentEuroBalance
            "USD" -> mCurrentUsdBalance
            "BGN" -> mCurrentBngBalance
            else -> mCurrentOtherCurrenciesBalance
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


    sealed class CurrencyEvent {
        class Success(val resultText: String) : CurrencyEvent()
        class Failure(val errorText: String) : CurrencyEvent()
        object Empty : CurrencyEvent()
    }
}
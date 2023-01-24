package com.example.mycurencyconverter.utils

import com.example.mycurencyconverter.data.model.Rates

object Utils {
    const  val BASE_URL = "https://developers.paysera.com/tasks/api/"

    fun getRateForCurrency(currency: String, rates: Rates?) = when (currency) {
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
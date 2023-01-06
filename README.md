# My-currency-converter
# Technologies: Hilt, Coroutines, Retrofit, ViewModel, Flow
# Architecture: MVVM
# Language: Kotlin
# API: https://developers.paysera.com/tasks/api/currency-exchange-rates
# The Conversion rate is updated every 5 seconds.
# The user has wallets in EUR, USD and BGN as shown on the provided screen. If it's necessary we can add other wallets.
# The user has started balance of 1000 Euros (EUR) that can be converted to any currency. The amount will be taken from sell currency wallet
# and added to receive currency wallet (e.g. USD or BGN).
# The first 5 transactions are free. Thereafter the charge is 0.7$ for each transaction charged on base currency wallet.
# The user cannot go below zero during the conversion taking into account the fee.
# After the conversion the user can see an Alert Dialog with the amount and currency he is selling, amount and currency he is getting and a fee.
# The UI is very similar to the the screen pictures provided in requirement.

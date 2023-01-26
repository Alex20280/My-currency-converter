# My-currency-converter
<p>Technologies: Hilt, Coroutines, Retrofit, ViewModel, Flow</p>
<p>Architecture: MVVM</p>
<p>Language: Kotlin</p>
<p>The Conversion rate is updated every 5 seconds.</p>
<p>The user has wallets in EUR, USD and BGN as shown on the provided screen. If it's necessary we can add other wallets.</p>
<p>The user has started balance of 1000 Euros (EUR) that can be converted to any currency. The amount will be taken from sell currency wallet</p>
<p>and added to receive currency wallet (e.g. USD or BGN).</p>
<p>The first 5 transactions are free. Thereafter the charge is 0.7% for each transaction charged on base currency wallet.</p>
<p>The user cannot go below zero during the conversion (taking into account the fee).</p>
<p>After the conversion the user can see an Alert Dialog with the amount and currency he is selling, amount and currency he is getting and a fee.</p>
<p>The UI is very similar to the the screen pictures provided in requirement.</p>


![myimage-alt-tag](https://imgur.com/a/oJ5P0OK.png)
![myimage-alt-tag](https://imgur.com/sxe6OjP.png)

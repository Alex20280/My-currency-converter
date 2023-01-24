package com.example.mycurencyconverter.presentation

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mycurencyconverter.R
import com.example.mycurencyconverter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class CurrencyActivity : AppCompatActivity() {

    private val viewModel: CurrencyViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var handler: Handler = Handler()
    private var runnable: Runnable? = null
    private var delay = 5000
    private var amountOfFee = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initViews()
        initSpinner(binding.sellSpinner)
        initSpinner(binding.receiveSpinner)
        initListener()
        observe()

    }

    override fun onResume() {
        startHandler(handler)
        super.onResume()
    }

    override fun onPause() {
        removeHandler(handler)
        super.onPause()
    }

    private fun removeHandler(handler: Handler) {
        handler.removeCallbacks(runnable!!)
    }

    private fun startHandler(handler: Handler) {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            loader()
        }.also { runnable = it }, delay.toLong())
    }

    private fun loader() {
        viewModel.startLoader()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyEvent.Success -> {
                        binding.receiveEditText.setText(getString(R.string.plus_sign).plus(event.resultText))
                        binding.receiveEditText.setTextColor(resources.getColor(R.color.light_green))
                        showDialog("You have converted ${binding.sellEditText.text} ${binding.sellSpinner.selectedItem} to ${binding.receiveEditText.text} ${binding.receiveSpinner.selectedItem}. Commission Fee -  $amountOfFee ${binding.sellSpinner.selectedItem}.")
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        Toast.makeText(this@CurrencyActivity,event.errorText,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun initListener() {
        binding.submitBtn.setOnClickListener {
            viewModel.convert(
                binding.sellEditText.text.toString(),
                binding.sellSpinner.selectedItem.toString(),
                binding.receiveSpinner.selectedItem.toString()
            ) {
                amountOfFee = it
            }
        }
    }

    private fun initViews() {
        lifecycleScope.launchWhenStarted {
            viewModel.euroBalance.collectLatest { euroWallet ->
                binding.euroTv.text =
                    BigDecimal(euroWallet).setScale(2, RoundingMode.HALF_EVEN).toString()
                        .plus(getString(R.string.euro))
            }

        }

        lifecycleScope.launchWhenStarted {
            viewModel.usdBalance.collectLatest { usdWallet ->
                binding.usdTv.text =
                    BigDecimal(usdWallet).setScale(2, RoundingMode.HALF_EVEN).toString()
                        .plus(getString(R.string.usd))

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bngBalance.collectLatest { bgnWallet ->
                binding.bgnTv.text =
                    BigDecimal(bgnWallet).setScale(2, RoundingMode.HALF_EVEN).toString()
                        .plus(getString(R.string.bgn))
            }
        }
    }

    private fun initToolbar() {
        binding.toolbar.root.title = getString(R.string.appLabel)
    }

    private fun initSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.currency_array,
            R.layout.my_selected_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.my_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun showDialog(text: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialog)
        val dialogText = dialog.findViewById(R.id.dialogTextTv) as TextView
        dialogText.text = text
        val yesBtn = dialog.findViewById(R.id.dialogDoneBtn) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
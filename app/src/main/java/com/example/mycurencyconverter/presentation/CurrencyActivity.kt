package com.example.mycurencyconverter.presentation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mycurencyconverter.R
import com.example.mycurencyconverter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode

//TODO write a documentation

@AndroidEntryPoint
class CurrencyActivity : AppCompatActivity() {


    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        dialog.dismiss()
    }
    private val viewModel: CurrencyViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

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

    private fun observe(){
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyEvent.Success -> {
                        binding.receiveEditText.setText(getString(R.string.plus_sign).plus(event.resultText))
                        binding.receiveEditText.setTextColor(resources.getColor(R.color.light_green))
                        showDialog("You have converted ${viewModel.currentEuroBalance} EUR to ${viewModel.convertedAmount} USD. Commission Fee - 0.70 EUR.")
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        //TODO (Implement some notification. Depends on usecase)
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
                binding.receiveSpinner.selectedItem.toString(),
            )
        }
    }

    private fun initViews() {
        //binding.euroTv.text = viewModel.currentBalance.toString().plus(getString(R.string.Euro))
            lifecycleScope.launchWhenStarted {
                viewModel.euroBalance.collect{ euroWallet ->
                    binding.euroTv.text = euroWallet.toString().plus(getString(R.string.euro))
                }

            }

        lifecycleScope.launchWhenStarted {
            viewModel.usdBalance.collect{ usdWallet ->
                //binding.usdTv.text = usdWallet.toString().plus(getString(R.string.usd))
                binding.usdTv.text = BigDecimal(usdWallet).setScale(2, RoundingMode.HALF_EVEN).toString().plus(getString(R.string.usd))

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bngBalance.collect{ bgnWallet ->
                //binding.bgnTv.text = bgnWallet.toString().plus(getString(R.string.bgn))
                binding.bgnTv.text = BigDecimal(bgnWallet).setScale(2, RoundingMode.HALF_EVEN).toString().plus(getString(R.string.bgn))
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

    private fun showDialog (text: String ) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.alert_dialog)
        val dialogText = dialog.findViewById(R.id.dialogTextTv) as TextView
        dialogText.text = text
        //title = text
        val yesBtn = dialog.findViewById(R.id.dialogDoneBtn) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

/*        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        val alertDialog = builder.create()
        alertDialog.layoutInflater.inflate()

        with(builder)
        {
            setTitle(R.string.currency_converted)
            setMessage(text)
            setPositiveButton(getString(R.string.done), DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }*/
    }

/*    fun showNotificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Androidly Alert")
        builder.setMessage("We have a message")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(applicationContext,
                "Maybe", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }*/
}
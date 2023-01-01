package com.example.mycurencyconverter.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mycurencyconverter.R
import com.example.mycurencyconverter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyActivity : AppCompatActivity() {

    private val viewModel: CurrencyViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initViews()
/*        initSpinner(binding.sellSpinner)
        initSpinner(binding.receiveSpinner)*/

        binding.submitBtn.setOnClickListener {
            viewModel.convert(
                binding.sellEditText.text.toString(),
                binding.sellSpinner.selectedItem.toString(),
                binding.receiveSpinner.selectedItem.toString(),
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyEvent.Success -> {
                        binding.receiveEditText.setTextColor(getResources().getColor(R.color.linght_green))
                        //TODO
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        //TODO
                    }
                    else -> Unit
                }
            }
        }
/*        ArrayAdapter.createFromResource(
            this,
            R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.sellSpinner.adapter = adapter

            binding.sellSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                }
                override fun onNothingSelected(parent: AdapterView<*>) { }
            }

        }*/
    }

    private fun initViews() {
        binding.euroTv.text = viewModel.currentBalance.toString().plus("EUR")
    }

    private fun initToolbar() {
        binding.toolbar.root.title = "Currency converter"
    }

/*    private fun initSpinner(spinner: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            com.example.mycurencyconverter.R.array.currency_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
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
    }*/
}
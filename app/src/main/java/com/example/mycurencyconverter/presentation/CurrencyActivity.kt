package com.example.mycurencyconverter.presentation

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        initSpinner(binding.sellSpinner)
        initSpinner(binding.receiveSpinner)
        initListener()


        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyViewModel.CurrencyEvent.Success -> {
                        binding.receiveEditText.setTextColor(getResources().getColor(R.color.light_green))
                        //TODO
                    }
                    is CurrencyViewModel.CurrencyEvent.Failure -> {
                        //TODO
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
        binding.euroTv.text = viewModel.currentBalance.toString().plus(getString(R.string.Euro))
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
}
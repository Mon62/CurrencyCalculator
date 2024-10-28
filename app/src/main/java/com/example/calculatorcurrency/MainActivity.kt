package com.example.calculatorcurrency

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var selectedFirstCurrency: TextView
    private lateinit var selectedSecondCurrency: TextView
    private lateinit var exchangeRate: TextView
    private lateinit var firstSpinner: Spinner
    private lateinit var secondSpinner: Spinner
    private lateinit var valueFirstCurrency: EditText
    private lateinit var valueSecondCurrency: EditText
    private var sourceCurrencyState = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val items: Array<String> = arrayOf(
            "Việt Nam - VND",
            "Mỹ - USD",
            "Châu Âu - EUR",
            "Nhật Bản - JPY",
            "Anh - GBP",
            "Trung Quốc - CNY"
        )
        val arrayAdapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)

        firstSpinner = findViewById(R.id.spinner_first_currency)
        secondSpinner = findViewById(R.id.spinner_second_currency)
        selectedFirstCurrency = findViewById(R.id.selected_first_currency)
        selectedSecondCurrency = findViewById(R.id.selected_second_currency)
        valueFirstCurrency = findViewById(R.id.value_first_currency)
        valueSecondCurrency = findViewById(R.id.value_second_currency)
        exchangeRate = findViewById(R.id.exchange_rate)

        valueFirstCurrency.setText("0")
        valueSecondCurrency.setText("0")

        valueFirstCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                valueFirstCurrency.setTypeface(null, Typeface.BOLD)
                valueSecondCurrency.setTypeface(null, Typeface.NORMAL)
                sourceCurrencyState = 1
                updateExchangeRate()
            }
        }

        valueSecondCurrency.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                valueSecondCurrency.setTypeface(null, Typeface.BOLD)
                valueFirstCurrency.setTypeface(null, Typeface.NORMAL)
                sourceCurrencyState = 2
                updateExchangeRate()
            }
        }

        valueFirstCurrency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (sourceCurrencyState == 1) {
                    val input = s.toString().toFloatOrNull() ?: 0F
                    val rate = getExchangeRateToVND(selectedFirstCurrency.text.toString()) /
                            getExchangeRateToVND(selectedSecondCurrency.text.toString())
                    valueSecondCurrency.setText((input * rate).toString())
                }
            }
        })

        valueSecondCurrency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (sourceCurrencyState == 2) {
                    val input = s.toString().toFloatOrNull() ?: 0F
                    val rate = getExchangeRateToVND(selectedSecondCurrency.text.toString()) /
                            getExchangeRateToVND(selectedFirstCurrency.text.toString())
                    valueFirstCurrency.setText((input * rate).toString())
                }
            }
        })

        valueFirstCurrency.requestFocus()

        firstSpinner.run {
            adapter = arrayAdapter
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedFirstCurrency.text = items[position].substringAfterLast(" - ")
                    updateExchangeRate()
                    updateCurrencyValues(1)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        secondSpinner.run {
            adapter = arrayAdapter
            setSelection(1)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSecondCurrency.text = items[position].substringAfterLast(" - ")
                    updateExchangeRate()
                    updateCurrencyValues(2)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        updateExchangeRate()
    }

    private fun getExchangeRateToVND(currency: String): Float {
        var exchangeRateToVND = 1F
        when (currency) {
            "VND" -> exchangeRateToVND = 1F
            "USD" -> exchangeRateToVND = 25405.02F
            "EUR" -> exchangeRateToVND = 27461F
            "JPY" -> exchangeRateToVND = 165.39F
            "GBP" -> exchangeRateToVND = 32901.85F
            "CNY" -> exchangeRateToVND = 3562.32F
        }
        return exchangeRateToVND
    }

    private fun updateExchangeRate() {
        val sourceCurrencyRate = getExchangeRateToVND(
            if (sourceCurrencyState == 2) selectedSecondCurrency.text.toString() else selectedFirstCurrency.text.toString()
        )
        val targetCurrencyRate = getExchangeRateToVND(
            if (sourceCurrencyState == 2) selectedFirstCurrency.text.toString() else selectedSecondCurrency.text.toString()
        )

        val rate = sourceCurrencyRate / targetCurrencyRate
        val formattedRate =
            DecimalFormat("#.#########", DecimalFormatSymbols(Locale.getDefault())).format(rate)

        exchangeRate.text = String.format(
            Locale.getDefault(),
            "1 %s = %s %s",
            if (sourceCurrencyState == 2) selectedSecondCurrency.text else selectedFirstCurrency.text,
            formattedRate,
            if (sourceCurrencyState == 2) selectedFirstCurrency.text else selectedSecondCurrency.text
        )
    }

    private fun updateCurrencyValues(index: Int) {
        if (index == 1) {
            val input = valueFirstCurrency.text.toString().toFloatOrNull() ?: 0F
            val rate = getExchangeRateToVND(selectedFirstCurrency.text.toString()) /
                    getExchangeRateToVND(selectedSecondCurrency.text.toString())
            valueSecondCurrency.setText((input * rate).toString())
        } else {
            val input = valueSecondCurrency.text.toString().toFloatOrNull() ?: 0F
            val rate = getExchangeRateToVND(selectedSecondCurrency.text.toString()) /
                    getExchangeRateToVND(selectedFirstCurrency.text.toString())
            valueFirstCurrency.setText((input * rate).toString())        }
    }
}
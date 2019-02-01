package com.example.dda.testcar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.app_bar_layout.*

class AddEditCarActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var editPriceView: EditText
    private lateinit var tvAddEdit: TextView
    private lateinit var s1: Spinner
    private lateinit var s2: Spinner
    private var listModels: Array<String> = arrayOf("Выберете модель...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_car)

        val brandList: Array<String> = resources.getStringArray(R.array.brands)

        editPriceView = findViewById(R.id.edit_price)
        tvAddEdit = findViewById(R.id.tvAddEdit)
        s1 = findViewById(R.id.sp_brand)
        s2 = findViewById(R.id.sp_model)

        if (intent.hasExtra("id")) {
            tvAddEdit.text = "Edit Car"
            val posBrand = brandList.indexOf(intent.getStringExtra("brand"))
            selectedBrand(posBrand)
            s1.setSelection(posBrand)
            editPriceView.setText(intent.getIntExtra("price", 1).toString())
        } else {
            tvAddEdit.text = "Add Car"
        }

        s1.onItemSelectedListener = this

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            saveCar()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedBrand(position)
        val adt: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, listModels)
        s2.adapter = adt
        s2.setSelection(listModels.indexOf(intent.getStringExtra("model")))
    }

    private fun selectedBrand(pos: Int) {
        if (pos == 0) {
            return
        }
        if (pos == 1) {
            listModels = arrayOf("Выберите модель...", "bmw1", "bmw2", "bmw3", "bmw4")
        }
        if (pos == 2) {
            listModels = arrayOf("Выберите модель...", "audi1", "audi2", "audi3", "audi4")
        }
        if (pos == 3) {
            listModels = arrayOf("Выберите модель...", "lada1", "lada2", "lada3", "lada4")
        }
    }

    private fun saveCar() {
        val replyIntent = Intent()
        if (s1.selectedItemPosition == 0 || s2.selectedItemPosition == 0 || TextUtils.isEmpty(editPriceView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        } else {
            replyIntent.putExtra("brand", s1.selectedItem.toString())
            replyIntent.putExtra("model", s2.selectedItem.toString())
            replyIntent.putExtra("price", editPriceView.text.toString().toInt())

            val id = intent.getIntExtra("id", -1)
            if (id != -1) {
                replyIntent.putExtra("id", id)
            }
            setResult(Activity.RESULT_OK, replyIntent)
        }
        finish()
    }
}

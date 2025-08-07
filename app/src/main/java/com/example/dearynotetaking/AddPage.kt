package com.example.dearynotetaking

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddPage : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextDate: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextUpdate: EditText
    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datepicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Do something with selected date
                editTextDate.text = "$dayOfMonth/${month + 1}/$year"
            }, year, month, day
        )
        datepicker.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_page)
        dbHelper = DatabaseHelper(this)
        editTextDescription = findViewById<EditText>(R.id.editTextDescription)
        editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        editTextDate = findViewById<Button>(R.id.editTextDate)
        editTextUpdate = findViewById<EditText>(R.id.editTextId)
        editTextDate.setOnClickListener {
            showDatePicker()
        }
        val saveButton = findViewById<FloatingActionButton>(R.id.save_new)
        saveButton.setOnClickListener {
            insertData()
        }
    }

    private fun insertData(){
        val title = editTextTitle.text.toString()
        val date = editTextDate.text.toString()
        val description = editTextDescription.text.toString()
        if(title.isNotBlank() && description.isNotBlank() && date.isNotBlank()){
            val id = dbHelper.insertData(date,title,description)
            if(id > 0){
                Toast.makeText(this,"Deary page added!",Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this,"Couldn't make a page",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
        }
        editTextUpdate.visibility = View.GONE
    }
    private fun hideKeyboard(){
        val view = this.currentFocus
        if(view!=null) {
            val key = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            key.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
package com.example.dearynotetaking

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.dearynotetaking.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var notes: MutableList<Note>
    private lateinit var fabSearch: FloatingActionButton

    private var isDeleteMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        notes = dbHelper.readData().toMutableList()

        adapter = NotesAdapter(
            this,
            notes,
            { clickedNote ->
                if (!isDeleteMode) {
                    val intent = Intent(this, DetailScreen::class.java)
                    intent.putExtra("note_id", clickedNote.id)
                    intent.putExtra("note_date", clickedNote.date)
                    intent.putExtra("note_title", clickedNote.title)
                    intent.putExtra("note_desc", clickedNote.description)
                    intent.putExtra("note_img", clickedNote.imagePath)
                    startActivity(intent)
                }
            },
            {
                toggleDeleteMode(true)
            },
            { clickedNote ->
                dbHelper.deleteData(clickedNote.id.toString())
                notes.remove(clickedNote)
                adapter.notifyDataSetChanged()
                if (notes.isEmpty()) {
                    toggleDeleteMode(false)
                }
            }
        )

        binding.gridView.adapter = adapter

        // Floating Action Button for searching
        fabSearch = findViewById(R.id.fab_search)
        fabSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Fetching Current Date for the Title in Main Page
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = formatter.format(date)
        val dateButton = findViewById<Button>(R.id.date_button)
        dateButton.text = formattedDate

        // Set a click listener for the date button to show the date picker
        dateButton.setOnClickListener {
            showDatePicker()
        }

        binding.fab.setOnClickListener {
            if (isDeleteMode) {
                toggleDeleteMode(false)
            } else {
                val intent = Intent(this, AddPage::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datepicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, dayOfMonth)
                }.time
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedSelectedDate = formatter.format(selectedDate)

                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search_query", formattedSelectedDate)
                startActivity(intent)
            },
            year, month, day
        )
        datepicker.show()
    }

    private fun toggleDeleteMode(enable: Boolean) {
        isDeleteMode = enable
        adapter.setDeleteMode(isDeleteMode)

        // Hide the search FAB when in delete mode
        if (enable) {
            fabSearch.visibility = View.GONE
        } else {
            fabSearch.visibility = View.VISIBLE
        }

        if (isDeleteMode) {
            binding.fab.setImageResource(R.drawable.baseline_check_24)
        } else {
            binding.fab.setImageResource(R.drawable.baseline_add_40)
        }
    }

    override fun onResume() {
        super.onResume()
        notes.clear()
        notes.addAll(dbHelper.readData())
        adapter.notifyDataSetChanged()
        toggleDeleteMode(false)
    }
}
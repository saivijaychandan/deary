package com.example.dearynotetaking

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.dearynotetaking.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var notes: MutableList<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        notes = dbHelper.readData().toMutableList()  // ✅ direct call

        adapter = NotesAdapter(this, notes) { clickedNote ->
            val intent = Intent(this, DetailScreen::class.java)
            intent.putExtra("note_id", clickedNote.id)
            intent.putExtra("note_date", clickedNote.date)
            intent.putExtra("note_title", clickedNote.title)
            intent.putExtra("note_desc", clickedNote.description)
            intent.putExtra("note_img", clickedNote.imagePath)
            startActivity(intent)
        }

        binding.gridView.adapter = adapter

        // Fetching Current Date for the Title in Main Page
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val formattedDate = formatter.format(date)
        val dateButton = findViewById<Button>(R.id.date_button)
        dateButton.text = formattedDate

        // Floating Action Button That Takes You To Add New
        val fab = findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddPage::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        notes.clear()
        notes.addAll(dbHelper.readData())  // ✅ reload directly
        adapter.notifyDataSetChanged()
    }
}

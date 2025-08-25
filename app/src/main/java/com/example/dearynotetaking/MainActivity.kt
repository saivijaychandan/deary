package com.example.dearynotetaking

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

    private var isDeleteMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        notes = dbHelper.readData().toMutableList()

        // Initialize adapter with the new onDeleteClick lambda
        adapter = NotesAdapter(
            this,
            notes,
            { clickedNote ->
                // on item click for navigation, only active if not in delete mode
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
                // This is the long-press listener from the adapter.
                // It will be triggered from the adapter itself.
                toggleDeleteMode(true)
            },
            { clickedNote ->
                // Delete button listener from adapter
                dbHelper.deleteData(clickedNote.id.toString())
                notes.remove(clickedNote)
                adapter.notifyDataSetChanged()
                if (notes.isEmpty()) {
                    toggleDeleteMode(false)
                }
            }
        )

        binding.gridView.adapter = adapter

        // Fetching Current Date for the Title in Main Page
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val formattedDate = formatter.format(date)
        val dateButton = findViewById<Button>(R.id.date_button)
        dateButton.text = formattedDate

        // Floating Action Button
        binding.fab.setOnClickListener {
            if (isDeleteMode) {
                toggleDeleteMode(false) // Exit delete mode
            } else {
                val intent = Intent(this, AddPage::class.java)
                startActivity(intent)
            }
        }
    }

    private fun toggleDeleteMode(enable: Boolean) {
        isDeleteMode = enable
        adapter.setDeleteMode(isDeleteMode)

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
        // Ensure to exit delete mode when returning to the screen
        toggleDeleteMode(false)
    }
}
package com.example.dearynotetaking

import android.annotation.SuppressLint
import android.widget.ImageView
import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class SearchActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: NotesAdapter
    private lateinit var notes: MutableList<Note>
    private lateinit var searchView: SearchView
    private lateinit var searchGridView: GridView

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        dbHelper = DatabaseHelper(this)
        notes = mutableListOf()

        searchView = findViewById(R.id.searchView)
        searchGridView = findViewById(R.id.searchGridView)
        val searchIcon = searchView.findViewById<ImageView>(
            searchView.context.resources.getIdentifier(
                "android:id/search_mag_icon",
                null,
                null
            )
        )
        // Fix for icon color
        searchIcon.setColorFilter(
            ContextCompat.getColor(this, R.color.dark_gray)
        )

        val searchEditText = searchView.findViewById<EditText>(
            searchView.context.resources.getIdentifier(
                "android:id/search_src_text",
                null,
                null
            )
        )

        val font = ResourcesCompat.getFont(this, R.font.la_belle_aurore)
        searchEditText.typeface = font
        searchEditText.setTextColor(ContextCompat.getColor(this, R.color.black))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.dark_gray))


        adapter = NotesAdapter(
            this,
            notes,
            { clickedNote ->
                val intent = Intent(this, DetailScreen::class.java)
                intent.putExtra("note_id", clickedNote.id)
                intent.putExtra("note_date", clickedNote.date)
                intent.putExtra("note_title", clickedNote.title)
                intent.putExtra("note_desc", clickedNote.description)
                intent.putExtra("note_img", clickedNote.imagePath)
                startActivity(intent)
            },
            {},
            {}
        )

        searchGridView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText.orEmpty())
                return true
            }
        })

        // This is the important part
        val initialQuery = intent.getStringExtra("search_query")
        if (initialQuery != null) {
            // Set the query and submit it to trigger the search
            searchView.setQuery(initialQuery, true)
        } else {
            // If no initial query, display all notes
            filterNotes("")
        }

    }

    private fun filterNotes(query: String) {
        notes.clear()
        if (query.isBlank()) {
            notes.addAll(dbHelper.readData())
        } else {
            notes.addAll(dbHelper.searchNotes(query))
        }
        adapter.notifyDataSetChanged()
    }
}
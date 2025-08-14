package com.example.dearynotetaking

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
        //Static String
//        val titleArr = arrayOf("Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 5", "Title 6","Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 5", "Title 6")
//        val imageArr = arrayOf(R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image,R.drawable.sample_image)
//        val dateArr = arrayOf("13/11/24","2/1/25","3/2/25","7/4/25","15/5/25","23/7/25","13/11/24","2/1/25","3/2/25","7/4/25","15/5/25","23/7/25")
//        val itemCount = minOf(titleArr.size, imageArr.size, dateArr.size)
        notes = displayData().toMutableList()
        adapter = NotesAdapter(this, notes){
            clickedNote -> Toast.makeText(this,"You clicked on ${clickedNote.title}",Toast.LENGTH_SHORT).show()
        }
        binding.gridView.adapter = adapter


        //Fetching Current Date for the Title in Main Page
        val calendar = Calendar.getInstance()
        val date = calendar.time
        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val formattedDate = formatter.format(date)
        val dateButton = findViewById<Button>(R.id.date_button)
        dateButton.text = formattedDate

        //Floating Action Button That Takes You To Add New
        val fab = findViewById<View>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddPage::class.java)
            startActivity(intent)
        }
    }
    private fun displayData(): List<Note> {
        val cursor = dbHelper.readData()
        val notesList = mutableListOf<Note>()

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATE))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESC))

                val note = Note(
                    id = id.toInt(),
                    title = title.toString(),
                    date = date.toString(),
                    imageResId = R.drawable.sample_image,
                    description = description.toString()
                )
                notesList.add(note)
            }while(cursor.moveToNext())
        }
        cursor.close()
        return notesList
    }

    override fun onResume() {
        super.onResume()
        notes.clear()
        notes.addAll(displayData())
        adapter.notifyDataSetChanged()
    }
}

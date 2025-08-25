package com.example.dearynotetaking

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import android.widget.ImageButton

class DetailScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_screen)

        // ✅ Retrieve all extras, including noteId
        val noteId = intent.getIntExtra("note_id", -1)
        val noteDate = intent.getStringExtra("note_date")
        val noteTitle = intent.getStringExtra("note_title")
        val noteDesc = intent.getStringExtra("note_desc")
        val noteImg = intent.getStringExtra("note_img")

        val displayDate = findViewById<TextView>(R.id.display_date)
        val displayTitle = findViewById<TextView>(R.id.display_title)
        val displayDesc = findViewById<TextView>(R.id.display_desc)
        val displayImage = findViewById<ShapeableImageView>(R.id.display_image)
        val editButton = findViewById<ImageButton>(R.id.display_edit)

        editButton.setOnClickListener {
            val intent = Intent(this, AddPage::class.java)
            intent.putExtra("isEdit", true)
            intent.putExtra("note_id", noteId)  // ✅ now works
            intent.putExtra("note_date", noteDate)
            intent.putExtra("note_title", noteTitle)
            intent.putExtra("note_desc", noteDesc)
            intent.putExtra("note_img", noteImg)
            startActivity(intent)
        }

        displayDate.text = noteDate
        displayTitle.text = noteTitle
        displayDesc.text = noteDesc

        if (!noteImg.isNullOrEmpty()) {
            try {
                val bitmap = BitmapFactory.decodeFile(noteImg)
                if (bitmap != null) {
                    displayImage.setImageBitmap(bitmap)

                    // Image Viewer with file path
                    displayImage.setOnClickListener {
                        val intent = Intent(this, ImageViewerActivity::class.java)
                        intent.putExtra("imagePath", noteImg)
                        startActivity(intent)
                    }
                } else {
                    displayImage.setImageResource(R.drawable.sample_image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                displayImage.setImageResource(R.drawable.sample_image)
            }
        } else {
            displayImage.setImageResource(R.drawable.sample_image)
        }

    }
}

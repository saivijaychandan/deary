package com.example.dearynotetaking

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val fullscreenImage: ImageView = findViewById(R.id.fullscreen_image)

        // Enable ActionBar back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check if we got a file path or a resource id
        val imagePath = intent.getStringExtra("imagePath")
        val imageResId = intent.getIntExtra("imageRes", 0)
        val imageUri = intent.getStringExtra("imageUri")

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }

        if (!imagePath.isNullOrEmpty()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            fullscreenImage.setImageBitmap(bitmap)
        } else if (!imageUri.isNullOrEmpty()) {
            fullscreenImage.setImageURI(imageUri.toUri())
        } else if (imageResId != 0) {
            fullscreenImage.setImageResource(imageResId)
        }

    }

    // Handle ActionBar back arrow
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

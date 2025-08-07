package com.example.dearynotetaking

import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ViewHolder(view: View) {
    val imageView: ImageView = view.findViewById(R.id.image_note)
    val dateTextView: TextView = view.findViewById(R.id.text_date)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
    // val descriptionTextView: TextView = view.findViewById(R.id.text_description)
}

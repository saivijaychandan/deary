package com.example.dearynotetaking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import android.net.Uri

class NotesAdapter(
    private val context: Context,
    private val notes: MutableList<Note>,
    private val onItemClick: (Note) -> Unit,
    private val onLongPress: () -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : BaseAdapter() {

    private var inflater: LayoutInflater? = null
    private var deleteMode = false

    override fun getCount(): Int = notes.size
    override fun getItem(position: Int): Note = notes[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun setDeleteMode(enabled: Boolean) {
        deleteMode = enabled
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (inflater == null) {
            inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            view = inflater!!.inflate(R.layout.item_note, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val note = notes.get(position)

        holder.cardView.setOnClickListener {
            onItemClick(note)
        }

        holder.cardView.setOnLongClickListener {
            onLongPress()
            true
        }

        // Set visibility of the delete X
        holder.deleteButton.visibility = if (deleteMode) View.VISIBLE else View.GONE

        // Set click listener for the delete X
        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }

        holder.titleTextView.text = note.title
        holder.dateTextView.text = note.date

        if (!note.imagePath.isNullOrEmpty()) {
            val imageFile = File(note.imagePath)
            if (imageFile.exists()) {
                holder.imageView.setImageURI(Uri.fromFile(imageFile))
            } else {
                holder.imageView.setImageResource(R.drawable.sample_image)
            }
        } else {
            holder.imageView.setImageResource(R.drawable.sample_image)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val imageView: ShapeableImageView = view.findViewById(R.id.image_note)
        val titleTextView: TextView = view.findViewById(R.id.text_title)
        val dateTextView: TextView = view.findViewById(R.id.text_date)
        val deleteButton: ImageView = view.findViewById(R.id.imageViewDeleteX) // The old ID
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
    }


}
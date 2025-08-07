package com.example.dearynotetaking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class NotesAdapter(
    private val context: Context,
    private val notes: MutableList<Note>
) : BaseAdapter() {

    private lateinit var dbHelper: DatabaseHelper

    private var inflater: LayoutInflater? = null

    override fun getCount(): Int = notes.size

    override fun getItem(position: Int): Note = notes[position]

    override fun getItemId(position: Int): Long = position.toLong()

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


        val note = notes[position]

        val deleteBtn = view.findViewById<ImageView>(R.id.imageViewDelete)
        if (note.showDelete) {
            deleteBtn.visibility = View.VISIBLE
        } else {
            deleteBtn.visibility = View.GONE
        }

        view.setOnLongClickListener {
            note.showDelete = !note.showDelete  // Toggle the delete button
            notifyDataSetChanged()              // Refresh the list
            true
        }

        deleteBtn.setOnLongClickListener {
            dbHelper.deleteData(note.id.toString())
            notes.removeAt(position)
            notifyDataSetChanged()
            true
        }

        holder.imageView.setImageResource(note.imageResId)
        holder.titleTextView.text = note.title
        holder.dateTextView.text = note.date

        return view
    }
}
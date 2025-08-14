package com.example.dearynotetaking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class NotesAdapter(
    private val context: Context,
    private val notes: MutableList<Note>,
    private val onItemClick: (Note) -> Unit
) : BaseAdapter() {

    private var inflater: LayoutInflater? = null
    private var deleteMode = false
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

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

        // Click opens note if not in delete mode
        view.setOnClickListener {
            if (!deleteMode) {
                onItemClick(note)
            }
        }

        // Long press toggles delete mode
        view.setOnLongClickListener {
            deleteMode = !deleteMode
            notifyDataSetChanged()
            true
        }

        // Show/hide delete button based on delete mode
        holder.deleteButton.visibility = if (deleteMode) View.VISIBLE else View.GONE

        // Delete action
        holder.deleteButton.setOnClickListener {
            dbHelper.deleteData(note.id.toString())
            notes.removeAt(position)
            deleteMode = false
            notifyDataSetChanged()
        }

        // Bind note data
        holder.imageView.setImageResource(note.imageResId)
        holder.titleTextView.text = note.title
        holder.dateTextView.text = note.date

        return view
    }

    fun exitDeleteMode() {
        deleteMode = false
        notifyDataSetChanged()
    }
}

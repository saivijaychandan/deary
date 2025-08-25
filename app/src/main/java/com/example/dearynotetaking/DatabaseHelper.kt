package com.example.dearynotetaking

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context:Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "DearyDb"
        const val DATABASE_VERSION = 2
        const val TABLE_NAME = "MyTable"
        const val COL_ID = "_id"
        const val COL_DATE = "date"
        const val COL_TITLE = "title"
        const val COL_DESC = "description"
        const val COL_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE TEXT,
                $COL_TITLE TEXT,
                $COL_DESC TEXT,
                $COL_IMAGE_PATH TEXT
            );
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_IMAGE_PATH TEXT")
        }
        // No need for a drop table, it will clear all user data on upgrade.
        // A simple ALTER TABLE is the correct way to handle schema changes.
    }

    fun insertData(date: String, title: String, description: String, imagePath: String?): Long{
        val db=writableDatabase
        val values= ContentValues().apply {
            put(COL_DATE,date)
            put(COL_TITLE,title)
            put(COL_DESC,description)
            put(COL_IMAGE_PATH, imagePath)
        }
        return db.insert(TABLE_NAME,null,values)
    }

    fun readData(): List<Note> {
        val db = readableDatabase
        val noteList = mutableListOf<Note>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        cursor.use { // Use 'use' for autoclosable
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(COL_ID))
                    val date = it.getString(it.getColumnIndexOrThrow(COL_DATE))
                    val title = it.getString(it.getColumnIndexOrThrow(COL_TITLE))
                    val desc = it.getString(it.getColumnIndexOrThrow(COL_DESC))
                    val imagePath = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_PATH))

                    // Correct order of parameters to match the Note data class
                    val note = Note(id, date, title, desc, imagePath)
                    noteList.add(note)
                } while (it.moveToNext())
            }
        }
        return noteList
    }

    fun updateData(id: String, date:String, title:String, description: String, imagePath: String?): Int{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DATE,date)
            put(COL_TITLE,title)
            put(COL_DESC,description)
            put(COL_IMAGE_PATH,imagePath)
        }
        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(id))
    }

    fun deleteData(id: String): Int{
        val db=writableDatabase
        return db.delete(TABLE_NAME,"$COL_ID=?",arrayOf(id))
    }
}
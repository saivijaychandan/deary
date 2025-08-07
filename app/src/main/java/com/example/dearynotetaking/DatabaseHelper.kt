package com.example.dearynotetaking

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context:Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "DearyDb"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "MyTable"
        const val COL_ID = "_id"
        const val COL_DATE = "date"
        const val COL_TITLE = "title"
        const val COL_DESC = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DATE TEXT,
                $COL_TITLE TEXT,
                $COL_DESC TEXT
            );
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(date: String, title: String, description: String): Long{
        val db=writableDatabase
        val values= ContentValues()
        values.put(COL_DATE,date)
        values.put(COL_TITLE,title)
        values.put(COL_DESC,description)
        return db.insert(TABLE_NAME,null,values)
    }

    fun readData(): Cursor{
        val db=readableDatabase
        val readDataQuery = "SELECT * FROM $TABLE_NAME"
        return db.rawQuery(readDataQuery,null)
    }

    fun updateData(id: String, date:String, title:String, description: String): Int{
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_DATE,date)
        values.put(COL_TITLE,title)
        values.put(COL_DESC,description)
        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(id.toString()))
    }

    fun deleteData(id: String): Int{
        val db=writableDatabase
        return db.delete(TABLE_NAME,"$COL_ID=?",arrayOf(id.toString()))
    }
}
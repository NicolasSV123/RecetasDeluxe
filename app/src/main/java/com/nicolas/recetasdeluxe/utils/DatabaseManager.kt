package com.nicolas.recetasdeluxe.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nicolas.recetasdeluxe.data.MyRecipe

class DatabaseManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(MyRecipe.SQL_CREATE_TABLE)
        db.execSQL(SQL_CREATE_FAVORITES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(MyRecipe.SQL_DROP_TABLE)
        db.execSQL(SQL_DROP_FAVORITES_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "RecetasDeluxe.db"

        const val TABLE_FAVORITES = "favorite_recipes"
        const val COLUMN_FAVORITE_ID = "recipe_id"

        const val SQL_CREATE_FAVORITES_TABLE =
            "CREATE TABLE $TABLE_FAVORITES ($COLUMN_FAVORITE_ID INTEGER PRIMARY KEY)"

        const val SQL_DROP_FAVORITES_TABLE = "DROP TABLE IF EXISTS $TABLE_FAVORITES"
    }
}

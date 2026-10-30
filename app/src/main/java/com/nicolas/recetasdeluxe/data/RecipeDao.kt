package com.nicolas.recetasdeluxe.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.nicolas.recetasdeluxe.utils.DatabaseManager

class RecipeDAO(val context: Context) {

    lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    // ==========================================
    // SECCIÓN 1: RECETAS PROPIAS (MyRecipe)
    // ==========================================

    fun getContentValues(recipe: MyRecipe): ContentValues {
        val values = ContentValues()
        values.put(MyRecipe.COLUMN_NAME, recipe.name)
        values.put(MyRecipe.COLUMN_INGREDIENTS, recipe.ingredients)
        values.put(MyRecipe.COLUMN_INSTRUCTIONS, recipe.instructions)
        return values
    }

    fun cursorToEntity(cursor: Cursor): MyRecipe {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(MyRecipe.COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(MyRecipe.COLUMN_NAME))
        val ingredients = cursor.getString(cursor.getColumnIndexOrThrow(MyRecipe.COLUMN_INGREDIENTS))
        val instructions = cursor.getString(cursor.getColumnIndexOrThrow(MyRecipe.COLUMN_INSTRUCTIONS))
        return MyRecipe(id, name, ingredients, instructions)
    }

    fun save(recipe: MyRecipe) {
        if (recipe.id == -1) {
            insert(recipe)
        } else {
            update(recipe)
        }
    }

    fun insert(recipe: MyRecipe) {
        open()
        val values = getContentValues(recipe)
        try {
            val newRowId = db.insertOrThrow(MyRecipe.TABLE_NAME, null, values)
            Log.i("DATABASE", "Inserted row with ID: $newRowId in ${MyRecipe.TABLE_NAME}")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun update(recipe: MyRecipe) {
        open()
        val values = getContentValues(recipe)
        try {
            db.update(
                MyRecipe.TABLE_NAME,
                values,
                "${MyRecipe.COLUMN_ID} = ${recipe.id}",
                null
            )
            Log.i("DATABASE", "Updated row with ID: ${recipe.id} in ${MyRecipe.TABLE_NAME}")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun delete(recipe: MyRecipe) {
        delete(recipe.id)
    }

    fun delete(id: Int) {
        open()
        try {
            db.delete(MyRecipe.TABLE_NAME, "${MyRecipe.COLUMN_ID} = $id", null)
            Log.i("DATABASE", "Deleted row with ID: $id in ${MyRecipe.TABLE_NAME}")
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun findById(id: Int): MyRecipe? {
        open()
        var recipe: MyRecipe? = null
        try {
            val cursor = db.query(
                MyRecipe.TABLE_NAME,
                null,
                "${MyRecipe.COLUMN_ID} = $id",
                null,
                null,
                null,
                null
            )
            if (cursor.moveToNext()) {
                recipe = cursorToEntity(cursor)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
        return recipe
    }

    fun findAllMyRecipes(): List<MyRecipe> {
        open()
        val recipeList = mutableListOf<MyRecipe>()
        try {
            val cursor = db.query(
                MyRecipe.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                "${MyRecipe.COLUMN_NAME} ASC"
            )
            while (cursor.moveToNext()) {
                val recipe = cursorToEntity(cursor)
                recipeList.add(recipe)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
        return recipeList
    }

    // ==========================================
    // SECCIÓN 2: FAVORITOS (IDs de la API)
    // ==========================================

    fun insertFavorite(recipeId: Int) {
        open()
        val values = ContentValues().apply {
            put(DatabaseManager.COLUMN_FAVORITE_ID, recipeId)
        }
        try {
            db.insertWithOnConflict(
                DatabaseManager.TABLE_FAVORITES,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            )
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun deleteFavorite(recipeId: Int) {
        open()
        try {
            db.delete(
                DatabaseManager.TABLE_FAVORITES,
                "${DatabaseManager.COLUMN_FAVORITE_ID} = $recipeId",
                null
            )
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
    }

    fun isFavorite(recipeId: Int): Boolean {
        open()
        var favorite = false
        try {
            val cursor = db.query(
                DatabaseManager.TABLE_FAVORITES,
                null,
                "${DatabaseManager.COLUMN_FAVORITE_ID} = $recipeId",
                null,
                null,
                null,
                null
            )
            favorite = cursor.count > 0
            cursor.close()
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
        return favorite
    }

    fun findAllFavoriteIds(): List<Int> {
        open()
        val favoriteIds = mutableListOf<Int>()
        try {
            val cursor = db.query(
                DatabaseManager.TABLE_FAVORITES,
                null,
                null,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseManager.COLUMN_FAVORITE_ID))
                favoriteIds.add(id)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("DATABASE", e.localizedMessage, e)
        } finally {
            close()
        }
        return favoriteIds
    }
}

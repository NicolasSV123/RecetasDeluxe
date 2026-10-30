package com.nicolas.recetasdeluxe.data

data class MyRecipe (
    val id: Int,
    var name: String,
    var ingredients: String,
    var instructions: String
) {
    companion object {
        const val TABLE_NAME = "my_recipes"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_INGREDIENTS = "ingredients"
        const val COLUMN_INSTRUCTIONS = "instructions"

        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_NAME TEXT," +
                    "$COLUMN_INGREDIENTS TEXT," +
                    "$COLUMN_INSTRUCTIONS TEXT" +
                    ")"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}

package com.nicolas.recetasdeluxe.data

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("cuisine") val cuisine: String,
    @SerializedName("prepTimeMinutes") val prepTimeMinutes: Int,
    @SerializedName("cookTimeMinutes") val cookTimeMinutes: Int,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("caloriesPerServing") val caloriesPerServing: Int,
    @SerializedName("servings") val servings: Int,
    @SerializedName("ingredients") val ingredients: List<String>,
    @SerializedName("instructions") val instructions: List<String>
)
data class RecipeResponse(
    @SerializedName("recipes") val recipes: List<Recipe>,
    @SerializedName("total") val total: Int,
    @SerializedName("skip") val skip: Int,
    @SerializedName("limit") val limit: Int
)
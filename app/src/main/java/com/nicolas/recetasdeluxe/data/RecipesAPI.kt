package com.nicolas.recetasdeluxe.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipesAPI {

    @GET("recipes")
    suspend fun getRecipesList(): RecipeResponse

    @GET("recipe")
    suspend fun getRecipesByID(@Query("id") recipeId: Int): Recipe

    companion object{

        fun getInstance(): RecipesAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RecipesAPI::class.java)
        }
    }
}
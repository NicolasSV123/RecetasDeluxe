package com.nicolas.recetasdeluxe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.databinding.ItemRecipeBinding
import com.nicolas.recetasdeluxe.utils.RecipeDiffUtil
import com.squareup.picasso.Picasso

class RecipeAdapter(
    var items: List<Recipe>,
    val onItemClick: (recipe: Recipe) -> Unit
) : RecyclerView.Adapter<RecipeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(layoutInflater, parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)
        holder.binding.recipeCard.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Recipe>) {
        val diffCallbacks = RecipeDiffUtil(items, dataSet)
        val diffResults = DiffUtil.calculateDiff(diffCallbacks)
        items = dataSet
        diffResults.dispatchUpdatesTo(this)
    }
}

class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root){
    fun render(recipe: Recipe){
        val context = itemView.context
        binding.nameRecipeTextView.text = recipe.name
        binding.countryChip.text = recipe.cuisine
        binding.difficultyChip.text = recipe.difficulty
        binding.caloriesChip.text = context.getString(R.string.recipe_calories, recipe.caloriesPerServing)
        binding.servingsChip.text = context.getString(R.string.recipe_servings, recipe.servings)

        val totalTimeMinutes = recipe.prepTimeMinutes + recipe.cookTimeMinutes
        binding.timeChip.text = context.getString(R.string.recipe_time, totalTimeMinutes)

        Picasso.get().load(recipe.image).into(binding.thumbnailImageView)
    }
}
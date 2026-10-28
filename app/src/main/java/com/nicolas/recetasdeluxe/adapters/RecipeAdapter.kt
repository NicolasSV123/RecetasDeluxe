package com.nicolas.recetasdeluxe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.databinding.ItemRecipeBinding
import com.nicolas.recetasdeluxe.utils.TaskDiffUtil

class RecipeAdapter(
    var items: List<Recipe>,
    val onItemClick: (position: Int) -> Unit
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
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataSet: List<Recipe>) {
        val diffCallbacks = TaskDiffUtil(items, dataSet)
        val diffResults = DiffUtil.calculateDiff(diffCallbacks)
        items = dataSet
        diffResults.dispatchUpdatesTo(this)
    }
}

class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root){
    fun render(recipe: Recipe){
        binding.nameRecipeTextView.text = recipe.name
    }
}
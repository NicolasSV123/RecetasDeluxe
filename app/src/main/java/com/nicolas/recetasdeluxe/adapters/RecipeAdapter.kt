package com.nicolas.recetasdeluxe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.databinding.ItemRecipeBinding
import com.nicolas.recetasdeluxe.utils.RecipeDiffUtil
import com.squareup.picasso.Picasso

// Adaptador para mostrar el catalogo principal de recetas de la API
class RecipeAdapter(
    var items: List<Recipe>,
    val onItemClick: (recipe: Recipe) -> Unit // Callback al pulsar una tarjeta para ver su detalle
) : RecyclerView.Adapter<RecipeViewHolder>(){

    // Inflamos el diseno XML de cada tarjeta de receta de la API
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecipeBinding.inflate(layoutInflater, parent, false)
        return RecipeViewHolder(binding)
    }

    // Asociamos cada receta de la lista con su tarjeta y gestionamos el clic
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)

        holder.binding.recipeCard.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = items.size

    // Actualiza la lista de recetas de forma eficiente usando DiffUtil
    fun updateData(dataSet: List<Recipe>) {
        val diffCallbacks = RecipeDiffUtil(items, dataSet)
        val diffResults = DiffUtil.calculateDiff(diffCallbacks)
        items = dataSet
        diffResults.dispatchUpdatesTo(this)
    }
}

// ViewHolder encargado de pintar los datos en cada tarjeta de receta
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

        // Comprobamos en SQLite si esta receta esta marcada como favorita para mostrar el icono de favorito
        val recipeDAO = RecipeDAO(context)
        val isFav = recipeDAO.isFavorite(recipe.id)
        if (isFav) {
            binding.favoriteImageView.visibility = View.VISIBLE
            binding.favoriteImageView.setImageResource(R.drawable.ic_favorite_added)
        } else {
            binding.favoriteImageView.visibility = View.GONE
        }

        // Cargamos la imagen de la receta usando Picasso
        Picasso.get().load(recipe.image).into(binding.thumbnailImageView)
    }
}

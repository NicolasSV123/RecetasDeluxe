package com.nicolas.recetasdeluxe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicolas.recetasdeluxe.data.MyRecipe
import com.nicolas.recetasdeluxe.databinding.OriginalRecipesBinding

class OriginalRecipeAdapter(
    var items: List<MyRecipe>,
    val onItemClick: (recipe: MyRecipe) -> Unit,
    val onEditClick: (recipe: MyRecipe) -> Unit,
    val onDeleteClick: (recipe: MyRecipe) -> Unit
) : RecyclerView.Adapter<OriginalRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OriginalRecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OriginalRecipesBinding.inflate(layoutInflater, parent, false)
        return OriginalRecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OriginalRecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)

        // Al hacer clic en la tarjeta o nombre -> Modo solo lectura
        holder.binding.originalRecipeCard.setOnClickListener {
            onItemClick(recipe)
        }

        // Al hacer clic en el botón de editar lápiz -> Modo edición
        holder.binding.editButton.setOnClickListener {
            onEditClick(recipe)
        }

        // Al hacer clic en la papelera -> Borrar
        holder.binding.deleteButton.setOnClickListener {
            onDeleteClick(recipe)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<MyRecipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}

class OriginalRecipeViewHolder(val binding: OriginalRecipesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun render(recipe: MyRecipe) {
        binding.nameOriginalRecipeTextView.text = recipe.name
    }
}

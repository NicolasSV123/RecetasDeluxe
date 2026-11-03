package com.nicolas.recetasdeluxe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicolas.recetasdeluxe.data.MyRecipe
import com.nicolas.recetasdeluxe.databinding.OriginalRecipesBinding

// Adaptador para mostrar la lista de recetas propias creadas por el usuario en el RecyclerView
class OriginalRecipeAdapter(
    var items: List<MyRecipe>,
    val onItemClick: (recipe: MyRecipe) -> Unit, // Callback al pulsar la tarjeta (ver)
    val onEditClick: (recipe: MyRecipe) -> Unit,   // Callback al pulsar el lapiz (editar)
    val onDeleteClick: (recipe: MyRecipe) -> Unit // Callback al pulsar la papelera (borrar)
) : RecyclerView.Adapter<OriginalRecipeViewHolder>() {

    // Inflamos el diseno XML de cada elemento de receta propia
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OriginalRecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = OriginalRecipesBinding.inflate(layoutInflater, parent, false)
        return OriginalRecipeViewHolder(binding)
    }

    // Asociamos cada receta propia con su vista y asignamos los clics a los botones
    override fun onBindViewHolder(holder: OriginalRecipeViewHolder, position: Int) {
        val recipe = items[position]
        holder.render(recipe)

        // Al hacer clic en la tarjeta completa (modo lectura)
        holder.binding.originalRecipeCard.setOnClickListener {
            onItemClick(recipe)
        }

        // Al hacer clic en el boton de editar lapiz (modo edicion)
        holder.binding.editButton.setOnClickListener {
            onEditClick(recipe)
        }

        // Al hacer clic en la papelera (borrar)
        holder.binding.deleteButton.setOnClickListener {
            onDeleteClick(recipe)
        }
    }

    override fun getItemCount(): Int = items.size

    // Actualiza los datos de la lista y refresca el RecyclerView
    fun updateData(newItems: List<MyRecipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}

// ViewHolder que contiene las referencias visuales de cada fila de receta propia
class OriginalRecipeViewHolder(val binding: OriginalRecipesBinding) : RecyclerView.ViewHolder(binding.root) {
    fun render(recipe: MyRecipe) {
        binding.nameOriginalRecipeTextView.text = recipe.name
    }
}

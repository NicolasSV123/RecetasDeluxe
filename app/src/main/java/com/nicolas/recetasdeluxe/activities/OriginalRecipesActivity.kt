package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.adapters.OriginalRecipeAdapter
import com.nicolas.recetasdeluxe.data.MyRecipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.databinding.ActivityOriginalRecipesBinding
import com.nicolas.recetasdeluxe.databinding.DialogAddRecipeBinding

class OriginalRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOriginalRecipesBinding
    private lateinit var adapter: OriginalRecipeAdapter
    private lateinit var recipeDAO: RecipeDAO
    private var recipeList: List<MyRecipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOriginalRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        adapter = OriginalRecipeAdapter(
            items = recipeList,
            onItemClick = { recipe ->
                // Modo solo lectura al pulsar el nombre / tarjeta
                showViewRecipeDialog(recipe)
            },
            onEditClick = { recipe ->
                // Modo edición al pulsar el botón del lápiz
                showRecipeDialog(recipe)
            },
            onDeleteClick = { recipe ->
                showDeleteConfirmationDialog(recipe)
            }
        )

        binding.recyclerViewRecipesOriginal.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecipesOriginal.adapter = adapter

        loadRecipes()

        binding.addTaskButton.setOnClickListener {
            showRecipeDialog()
        }

        // 👇 CÓDIGO DEL BOTTOM NAVIGATION 👇
        binding.bottomNavigationView.selectedItemId = R.id.menu_add_recipe

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                R.id.menu_add_recipe -> true
                else -> false
            }
        }
    }

    private fun loadRecipes() {
        recipeList = recipeDAO.findAllMyRecipes()
        adapter.updateData(recipeList)

        if (recipeList.isEmpty()) {
            binding.recyclerViewRecipesOriginal.visibility = View.GONE
        } else {
            binding.recyclerViewRecipesOriginal.visibility = View.VISIBLE
        }
    }

    // Diálogo de lectura (solo lectura)
    private fun showViewRecipeDialog(recipe: MyRecipe) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        // Deshabilitar la edición
        dialogBinding.nameEditText.isEnabled = false
        dialogBinding.ingredientsEditText.isEnabled = false
        dialogBinding.instructionsEditText.isEnabled = false

        MaterialAlertDialogBuilder(this)
            .setTitle(recipe.name)
            .setView(dialogBinding.root)
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Diálogo para crear o editar
    private fun showRecipeDialog(recipe: MyRecipe = MyRecipe(id = -1, name = "", ingredients = "", instructions = "")) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        val dialogTitle = if (recipe.id == -1) "Nueva receta" else "Editar receta"

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ -> }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val ingredients = dialogBinding.ingredientsEditText.text.toString().trim()
            val instructions = dialogBinding.instructionsEditText.text.toString().trim()

            if (name.isEmpty()) {
                dialogBinding.nameTextField.error = "No se pueden crear recetas vacías"
            } else {
                recipe.name = name
                recipe.ingredients = ingredients
                recipe.instructions = instructions

                recipeDAO.save(recipe)
                dialog.dismiss()
                loadRecipes()
            }
        }
    }

    private fun showDeleteConfirmationDialog(recipe: MyRecipe) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Eliminar receta")
            .setMessage("¿Estás seguro de que deseas eliminar \"${recipe.name}\"?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                recipeDAO.delete(recipe)
                loadRecipes()
                Toast.makeText(this, "Receta eliminada", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
